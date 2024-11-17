package ltd.ligma.vorovayka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Class<?> entityClass, UUID id) {
        super(String.format("Entity %s with ID %s not found", entityClass.getSimpleName(), id));
    }

    public NotFoundException(Class<?> entityClass, String key) {
        super(String.format("Entity %s with key %s not found", entityClass.getSimpleName(), key));
    }

    @NonNull
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
