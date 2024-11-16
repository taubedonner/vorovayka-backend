package ltd.ligma.vorovayka.service;

import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.model.OrderProduct;
import ltd.ligma.vorovayka.model.payload.ProductMeta;
import ltd.ligma.vorovayka.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    /**
     * @return first - order count, second - total rating
     */
    public ProductMeta loadProductMeta(UUID productId) {
        return orderProductRepository.loadProductMeta(productId);
    }

    public OrderProduct findByOrderIdAndProductId(UUID orderId, UUID productId) {
        return orderProductRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new NotFoundException(String.format("Could not find ordered product with oid '%s' and pid '%s'",
                        orderId, productId)));
    }

    public void rate(UUID orderId, UUID productId, Double rate) {
        OrderProduct orderProduct = findByOrderIdAndProductId(orderId, productId);
        orderProduct.setRate(rate);
        save(orderProduct);
    }

    public void save(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }
}
