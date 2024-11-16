package ltd.ligma.vorovayka.rest.advice;

import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.exception.ApiException;
import ltd.ligma.vorovayka.exception.ApiExceptionResponseBody;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponseBody> apiExceptionHandler(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(capture(ex));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> accessDeniedExceptionHandler() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponseBody> unknownExceptionHandler(Exception ex) {
        log.warn("This exception should be handled and wrapped in an ApiException! ({})",
                ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(captureUnknown(ex));
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ApiExceptionResponseBody body = new ApiExceptionResponseBody(ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("[%s] %s: %s", e.getObjectName(), e.getField(), e.getDefaultMessage())).toList());
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @Nullable
    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
                                                          HttpHeaders headers, HttpStatusCode status,
                                                          WebRequest request) {
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON).body(captureUnknown(ex));
    }

    private ApiExceptionResponseBody captureUnknown(Exception ex) {
        ApiExceptionResponseBody body = new ApiExceptionResponseBody(List.of(ex.getMessage()));
        log.error("HTTP Exception UID: {}, Details: {}", body.getId(), body.getDetails(), ex);
        return body;
    }

    private ApiExceptionResponseBody capture(ApiException ex) {
        ApiExceptionResponseBody body = ex.getResponseBody();
        log.error("API Exception UID: {}, Details: {}", body.getId(), body.getDetails(), ex);
        return body;
    }
}
