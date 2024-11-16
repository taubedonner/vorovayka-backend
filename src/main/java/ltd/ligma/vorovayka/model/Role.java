package ltd.ligma.vorovayka.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Table(name = "roles")
@Entity
public class Role {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 32)
    private Names name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    public enum Names {
        ROLE_ADMIN,
        ROLE_USER
    }
}
