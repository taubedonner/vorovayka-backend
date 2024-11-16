package ltd.ligma.vorovayka.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "product_types")
public class ProductType extends BaseEntity {
    @Column(length = 60)
    private String name;
}
