package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.model.OrderProduct;
import ltd.ligma.vorovayka.model.payload.ProductMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    @Query("select new ltd.ligma.vorovayka.model.payload.ProductMeta(count (op), avg (op.rate)) " +
            "from OrderProduct op " +
            "where op.product.id = ?1 " +
            "and op.order.state = ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum.COMPLETED")
    ProductMeta loadProductMeta(UUID id);

    Optional<OrderProduct> findByOrderIdAndProductId(UUID orderId, UUID productId);
}
