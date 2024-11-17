package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class RefreshTokenMisuseException extends ApiException {
    public RefreshTokenMisuseException(String error) {
        super(error);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.I_AM_A_TEAPOT;
    }
}
