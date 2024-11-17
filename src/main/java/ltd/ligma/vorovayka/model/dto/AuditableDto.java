package ltd.ligma.vorovayka.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditableDto {
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(value = "updatedAt", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
