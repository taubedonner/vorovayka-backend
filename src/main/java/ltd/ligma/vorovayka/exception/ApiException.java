package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.List;

public abstract class ApiException extends RuntimeException {
    private final ApiExceptionResponseBody body;

    public ApiException(String message) {
        this(List.of(message));
    }

    public ApiException(List<String> messages) {
        super(messages.toString());
        body = new ApiExceptionResponseBody(messages);
    }

    @NonNull
    public final ApiExceptionResponseBody getResponseBody() {
        return body;
    }

    @NonNull
    public abstract HttpStatus getStatus();
}
