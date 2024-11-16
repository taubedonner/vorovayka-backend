package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class TokenExpiredException extends ApiException {
    public TokenExpiredException(String error) {
        super(error);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
