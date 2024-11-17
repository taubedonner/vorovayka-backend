package ltd.ligma.vorovayka.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This method of transferring tokens is required
 * for devices that do not support Cookies.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenPairPayload {
    private String accessToken;
    private String refreshToken;
}
