package ltd.ligma.vorovayka.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_products")
public class OrderProduct extends BaseEntity {
    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Product product;

    @Schema(description = "Product price at the time of ordering")
    @Column(nullable = false, updatable = false)
    private BigDecimal capturedPrice = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private BigInteger quantity = BigInteger.ONE;

    private Double rate;
}
