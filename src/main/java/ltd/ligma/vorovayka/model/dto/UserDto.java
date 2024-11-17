package ltd.ligma.vorovayka.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ltd.ligma.vorovayka.model.payload.LoginPayload;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto extends LoginPayload {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Valid
    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    private List<String> roles;

    @NotBlank
    @Size(min = 2, max = 128)
    @JsonProperty("firstName")
    private String firstName;

    @Size(min = 2, max = 128)
    @JsonProperty("middleName")
    private String middleName;

    @NotBlank
    @Size(min = 2, max = 128)
    @JsonProperty("lastName")
    private String lastName;
}
