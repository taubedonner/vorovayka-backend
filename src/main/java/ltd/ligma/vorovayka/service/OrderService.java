package ltd.ligma.vorovayka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.config.props.OrderLogicConfig;
import ltd.ligma.vorovayka.config.statemachine.event.OrderEventEnum;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import ltd.ligma.vorovayka.exception.BadRequestException;
import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.filter.OrderFilter;
import ltd.ligma.vorovayka.model.*;
import ltd.ligma.vorovayka.model.dto.RateProductDto;
import ltd.ligma.vorovayka.model.dto.ReserveOrderDto;
import ltd.ligma.vorovayka.model.dto.SaveOrderProductDto;
import ltd.ligma.vorovayka.model.payload.OrderStateProjection;
import ltd.ligma.vorovayka.repository.OrderRepository;
import ltd.ligma.vorovayka.security.TokenPrincipal;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@EnableScheduling
@RequiredArgsConstructor
@EnableConfigurationProperties(OrderLogicConfig.class)
public class OrderService {
    private final UserService userService;
    private final OrderLogicConfig orderLogicConfig;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final StateMachineFactory<OrderStateEnum, OrderEventEnum> stateMachineFactory;

    public Order reserve(TokenPrincipal principal, ReserveOrderDto dto) {
        User user = userService.findById(principal.userId());
        Order order = new Order();
        order.setUser(user);
        mutateOrderDetails(order, dto);
        order.setReserveExpiresIn(LocalDateTime.now()
                .plus(orderLogicConfig.expirationTime(), orderLogicConfig.expirationTimeUnit()));
        order = orderRepository.save(order);
        mutateStateAndSave(order, OrderEventEnum.RESERVE);
        return order;
    }

    public Page<Order> findAll(OrderFilter filter, Pageable pageable) {
        return orderRepository.findAll(filter, pageable);
    }

    public Page<Order> findAll(TokenPrincipal principal, OrderFilter filter, Pageable pageable) {
        User user = userService.findById(principal.userId());
        return orderRepository.findAll(filter.and(byUser(user)), pageable);
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order with ID %s not found", id)));
    }

    public Order findById(TokenPrincipal principal, UUID id) {
        return orderRepository.findByIdAndUserId(id, principal.userId())
                .orElseThrow(() -> new NotFoundException(String.format("Order with ID %s not found", id)));
    }

    public Order editReserved(UUID id, ReserveOrderDto dto) {
        Order order = findById(id);
        mutateStateAndSave(order, OrderEventEnum.EDIT_RESERVED);
        mutateOrderDetails(order, dto);
        return orderRepository.save(order);
    }

    public Order editReserved(TokenPrincipal principal, UUID id, ReserveOrderDto dto) {
        Order order = findById(principal, id);
        mutateStateAndSave(order, OrderEventEnum.EDIT_RESERVED);
        mutateOrderDetails(order, dto);
        return orderRepository.save(order);
    }

    public void cancelReserve(TokenPrincipal principal, UUID id) {
        mutateStateAndSave(findById(principal, id), OrderEventEnum.CANCEL_RESERVE);
    }

    public void purchase(TokenPrincipal principal, UUID id) {
        mutateStateAndSave(findById(principal, id), OrderEventEnum.PURCHASE);
    }

    public void cancelPurchase(UUID id) {
        mutateStateAndSave(findById(id), OrderEventEnum.CANCEL_PURCHASE);
    }

    public void cancelPurchase(TokenPrincipal principal, UUID id) {
        mutateStateAndSave(findById(principal, id), OrderEventEnum.CANCEL_PURCHASE);
    }

    public void refund(UUID id) {
        mutateStateAndSave(findById(id), OrderEventEnum.REFUND);
    }

    public void transit(UUID id) {
        mutateStateAndSave(findById(id), OrderEventEnum.TRANSIT);
    }

    public void deliver(UUID id) {
        mutateStateAndSave(findById(id), OrderEventEnum.DELIVER);
    }

    public void complete(UUID id) {
        mutateStateAndSave(findById(id), OrderEventEnum.COMPLETE);
    }

    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    public void rate(TokenPrincipal principal, UUID oid, UUID pid, RateProductDto dto) {
        mutateStateAndSave(findById(principal, oid), OrderEventEnum.LEAVE_FEEDBACK);
        orderProductService.rate(oid, pid, dto.getRate());
    }

    private void mutateStateAndSave(Order order, OrderEventEnum event) {
        OrderStateEnum state = changeState(order, event);
        orderRepository.updateStateByIds(Set.of(order.getId()), state);
        order.setState(state);
    }

    private void mutateOrderDetails(Order order, ReserveOrderDto dto) {
        Set<OrderProduct> products = fillProducts(order, dto.getProducts());
        order.setAddress(dto.getAddress());
        order.getProducts().addAll(products);
        order.setTotal(calculateTotal(products));
    }

    private BigDecimal calculateTotal(Set<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(op -> op.getCapturedPrice().multiply(new BigDecimal(op.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderProduct> fillProducts(Order order, Set<SaveOrderProductDto> set) {
        return set.stream().map(s -> {
            Product p = productService.findById(s.getProductId());
            return new OrderProduct(order, p, p.getPrice(), s.getQuantity(), 0.);
        }).collect(Collectors.toSet());
    }

    private OrderStateEnum changeState(Order order, OrderEventEnum event) {
        return changeState(order.getId(), order.getState(), event);
    }

    private OrderStateEnum changeState(UUID orderId, OrderStateEnum savedState, OrderEventEnum event) {
        AtomicBoolean success = new AtomicBoolean(Boolean.FALSE);
        StateMachine<OrderStateEnum, OrderEventEnum> stateMachine = acquireStateMachine(orderId, savedState);

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build()))
                .subscribe(r -> success.set(r.getResultType() == StateMachineEventResult.ResultType.ACCEPTED));

        if (success.get()) return stateMachine.getState().getId();
        throw new BadRequestException(String.format("Could not change state from %s with event %s for Order %s",
                stateMachine.getState().getId(), event, orderId));
    }

    private StateMachine<OrderStateEnum, OrderEventEnum> acquireStateMachine(UUID id, OrderStateEnum savedState) {
        StateMachine<OrderStateEnum, OrderEventEnum> sm = stateMachineFactory.getStateMachine(id);
        sm.stopReactively().subscribe();
        sm.getStateMachineAccessor().doWithAllRegions((a) -> a.resetStateMachineReactively(
                new DefaultStateMachineContext<>(savedState, null, null, null)).subscribe());
        sm.startReactively().subscribe();
        return sm;
    }

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void mutateExpiredAndSave() {
        List<OrderStateProjection> orders = orderRepository
                .findAllByReserveExpiresInIsLessThanEqualAndState(LocalDateTime.now(), OrderStateEnum.RESERVED);
        if (!orders.isEmpty()) {
            log.info("Starting expiration audit of {} reserved orders", orders.size());
            orderRepository.updateStateByIds(orders
                    .stream()
                    .map(OrderStateProjection::id)
                    .collect(Collectors.toSet()), OrderStateEnum.RESERVE_EXPIRED);
        }
    }

    private Specification<Order> byUser(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Order_.user), user);
    }
}
