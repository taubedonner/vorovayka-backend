package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class UserAuthException extends ApiException {
    public UserAuthException() {
        super("Incorrect email or password");
    }

    public UserAuthException(String message) {
        super(message);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
