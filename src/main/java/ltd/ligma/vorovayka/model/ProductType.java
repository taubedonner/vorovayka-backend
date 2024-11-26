package ltd.ligma.vorovayka.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_types")
public class ProductType {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(length = 60)
    private String name;
}
