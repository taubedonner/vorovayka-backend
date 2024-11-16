package ltd.ligma.vorovayka.model;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "manufacturers")
public class Manufacturer extends BaseEntity {
    @Column(length = 60)
    private String name;
}
