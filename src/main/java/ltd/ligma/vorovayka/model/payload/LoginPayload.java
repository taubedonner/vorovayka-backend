package ltd.ligma.vorovayka.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPayload {
    @Schema(example = "balls@ligma.ltd")
    @Email
    @NotBlank
    @Size(max = 255)
    @JsonProperty("email")
    private String email;

    @Schema(example = "aboba228")
    @NotBlank
    @Size(max = 255)
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
