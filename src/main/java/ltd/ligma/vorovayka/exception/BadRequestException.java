package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.List;

public class BadRequestException extends ApiException {
    public BadRequestException(String error) {
        super(error);
    }

    public BadRequestException(List<String> errors) {
        super(errors);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
