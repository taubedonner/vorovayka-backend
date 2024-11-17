package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;
import ltd.ligma.vorovayka.model.payload.ClientMeta;

import java.time.LocalDateTime;

@Getter
@Setter
public class SessionDto extends ClientMeta {
    private LocalDateTime createdAt;

    private LocalDateTime expiresIn;

    Boolean isActive = false;
}
