package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class NotImplementedException extends ApiException {

    public NotImplementedException() {
        this("This operation is currently not supported");
    }

    public NotImplementedException(String message) {
        super(message);
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_IMPLEMENTED;
    }
}
