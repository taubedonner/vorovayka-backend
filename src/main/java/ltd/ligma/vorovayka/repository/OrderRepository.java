package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import ltd.ligma.vorovayka.model.Order;
import ltd.ligma.vorovayka.model.payload.OrderStateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUserId(UUID id, UUID uid);
    OrderStateProjection findOrderById(UUID id);
    List<OrderStateProjection> findAllByReserveExpiresInIsLessThanEqualAndState(LocalDateTime dateTime, OrderStateEnum state);
    @Modifying
    @Query("update Order o set o.state = ?2 where o.id in ?1")
    void updateStateByIds(Set<UUID> ids, OrderStateEnum state);
}
