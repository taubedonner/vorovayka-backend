package ltd.ligma.vorovayka.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.filter.OrderFilter;
import ltd.ligma.vorovayka.model.Order;
import ltd.ligma.vorovayka.model.dto.RateProductDto;
import ltd.ligma.vorovayka.model.dto.ReserveOrderDto;
import ltd.ligma.vorovayka.service.OrderService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order Endpoints")
@RequestMapping("orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @IsUser
    @PostMapping("my")
    @DocumentedOperation(desc = "Create new order with pre-purchase state by user access token", errors = HttpStatus.BAD_REQUEST)
    public Order reserve(@AuthenticationPrincipal Object principal, @Valid @RequestBody ReserveOrderDto dto) {
        return orderService.reserve(principal, dto);
    }

    @IsAdmin
    @GetMapping
    @DocumentedOperation(desc = "Find all created orders", errors = HttpStatus.BAD_REQUEST)
    public PagedModel<Order> findAll(@ParameterObject OrderFilter filter, @ParameterObject Pageable pageable) {
        return new PagedModel<>(orderService.findAll(filter, pageable));
    }

    @IsUser
    @GetMapping("my")
    @DocumentedOperation(desc = "Find user's created orders", errors = HttpStatus.BAD_REQUEST)
    public PagedModel<Order> findMyAll(@AuthenticationPrincipal Object principal, @ParameterObject OrderFilter filter, @ParameterObject Pageable pageable) {
        return new PagedModel<>(orderService.findAll(principal, filter, pageable));
    }

    @IsAdmin
    @GetMapping("{id}")
    @DocumentedOperation(desc = "Find any created order by ID", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public Order findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @IsUser
    @GetMapping("my/{id}")
    @DocumentedOperation(desc = "Find user's created order by ID", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public Order findMyById(@AuthenticationPrincipal Object principal, @PathVariable UUID id) {
        return orderService.findById(principal, id);
    }

    @IsAdmin
    @PutMapping("{id}")
    @DocumentedOperation(desc = "Edit any pre-purchased order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public Order editReserved(@PathVariable UUID id, @Valid @RequestBody ReserveOrderDto dto) {
        return orderService.editReserved(id, dto);
    }

    @IsUser
    @PutMapping("my/{id}")
    @DocumentedOperation(desc = "Edit user's pre-purchased order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public Order editMyReserved(@AuthenticationPrincipal Object principal, @PathVariable UUID id, @Valid @RequestBody ReserveOrderDto dto) {
        return orderService.editReserved(principal, id, dto);
    }

    @IsUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("my/{oid}/products/{pid}/rate")
    @DocumentedOperation(desc = "Set rating of product from completed order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void rate(@AuthenticationPrincipal Object principal, @PathVariable UUID oid, @PathVariable UUID pid, @Valid @RequestBody RateProductDto dto) {
        orderService.rate(principal, oid, pid, dto);
    }

    @IsUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("my/{id}/cancel-reserve")
    @DocumentedOperation(desc = "Cancel reserving user's reserved order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void cancelReserveMy(@AuthenticationPrincipal Object principal, @PathVariable UUID id) {
        orderService.cancelReserve(principal, id);
    }

    @IsUser
    @PatchMapping("my/{id}/purchase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Set purchase state for user's reserved order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void purchaseMy(@AuthenticationPrincipal Object principal, @PathVariable UUID id) {
        orderService.purchase(principal, id);
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("{id}/cancel-purchase")
    @DocumentedOperation(desc = "Cancel purchase for any purchased order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void cancelPurchase(@PathVariable UUID id) {
        orderService.cancelPurchase(id);
    }

    @IsUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("my/{id}/cancel-purchase") // TODO: Reject user's refund in IN_TRANSIT state
    @DocumentedOperation(desc = "Cancel purchase for user's purchased order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void cancelPurchase(@AuthenticationPrincipal Object principal, @PathVariable UUID id) {
        orderService.cancelPurchase(principal, id);
    }

    @IsAdmin
    @PatchMapping("{id}/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Start refund process for any purchase-cancelled order", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void refund(@PathVariable UUID id) {
        orderService.refund(id);
    }

    @IsAdmin
    @PatchMapping("{id}/transit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Start purchased order delivering", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void transit(@PathVariable UUID id) {
        orderService.transit(id);
    }

    @IsAdmin
    @PatchMapping("{id}/deliver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "End purchased order delivering", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void deliver(@PathVariable UUID id) {
        orderService.deliver(id);
    }

    @IsAdmin
    @PatchMapping("{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Complete order delivery", errors = {HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND})
    public void complete(@PathVariable UUID id) {
        orderService.complete(id);
    }

    @IsAdmin
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Delete order by ID", errors = HttpStatus.BAD_REQUEST)
    public void delete(@PathVariable UUID id) {
        orderService.delete(id);
    }
}
