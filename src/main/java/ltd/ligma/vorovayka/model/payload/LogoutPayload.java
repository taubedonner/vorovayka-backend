package ltd.ligma.vorovayka.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutPayload {
    @JsonProperty(value = "refreshToken")
    private String refreshToken;
}
