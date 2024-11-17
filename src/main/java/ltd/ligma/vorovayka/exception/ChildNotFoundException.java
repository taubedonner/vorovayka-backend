package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class ChildNotFoundException extends ApiException {
    public ChildNotFoundException(String message) {
        super(message);
    }

    public ChildNotFoundException(Class<?> entityClass, UUID id) {
        super(String.format("Entity %s with ID %s not found", entityClass.getSimpleName(), id));
    }

    public ChildNotFoundException(Class<?> entityClass, String key) {
        super(String.format("Related entity %s with key %s not found", entityClass.getSimpleName(), key));
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
