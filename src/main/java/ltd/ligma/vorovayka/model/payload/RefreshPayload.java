package ltd.ligma.vorovayka.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshPayload {
    @JsonProperty(value = "refreshToken")
    private String refreshToken;

    @Schema(description = "Client's unique device / browser fingerprint")
    @NotBlank
    @Size(max = 255)
    @JsonProperty(value = "fingerprint")
    private String fingerprint;
}
