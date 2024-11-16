package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.List;

public class UnsupportedMediaTypeException extends ApiException {
    public UnsupportedMediaTypeException(String message) {
        super(message);
    }

    public UnsupportedMediaTypeException(String providedType, List<String> supportedTypes) {
        super(String.format("Unsupported media type '%s'. Supported types: '%s'", providedType, supportedTypes));
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }
}
