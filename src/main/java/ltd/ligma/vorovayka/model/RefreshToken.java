package ltd.ligma.vorovayka.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseAuditable {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(name = "token", updatable = false)
    private String token;

    @Column(name = "expires_in", updatable = false, nullable = false)
    private LocalDateTime expiresIn;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    @Column(name = "ip")
    private String ip;

    @Column(name = "cpu")
    private String cpu;

    @Column(name = "system")
    private String system;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "client")
    private String client;
}
