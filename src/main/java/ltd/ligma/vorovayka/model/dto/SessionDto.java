package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;
import ltd.ligma.vorovayka.model.payload.ClientMeta;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SessionDto extends ClientMeta {
    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime expiresIn;

    Boolean isActive = false;
}
