package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
