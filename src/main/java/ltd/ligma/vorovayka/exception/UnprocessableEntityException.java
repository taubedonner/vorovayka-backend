package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class UnprocessableEntityException extends ApiException {
    public UnprocessableEntityException(String message) {
        super(message);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
