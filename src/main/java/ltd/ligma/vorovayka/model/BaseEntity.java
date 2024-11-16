package ltd.ligma.vorovayka.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
@MappedSuperclass
public class BaseEntity extends BaseAuditable implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

//    @Column(name = "enabled", nullable = false)
//    private Boolean enabled = Boolean.TRUE;
}
