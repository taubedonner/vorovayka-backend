package ltd.ligma.vorovayka.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(optional = false)
    private ProductType type;

    @ManyToOne(optional = false)
    private Manufacturer manufacturer;

    @Schema(description = "Average rating from completed orders")
    @Transient
    private Double rating = 0.;

    @Schema(description = "Number of completed orders containing this product")
    @Transient
    private Long orderCount = 0L;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "text")
    private String description;
}
