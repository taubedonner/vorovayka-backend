package ltd.ligma.vorovayka.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ltd.ligma.vorovayka.exception.ApiExceptionResponseBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver exceptionResolver;

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPointImpl(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver, ObjectMapper objectMapper) {
        this.exceptionResolver = exceptionResolver;
        this.objectMapper = objectMapper;
    }

    /** Just returns HTTP 401 on every AuthenticationException */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        if (request.getAttribute("javax.servlet.error.exception") != null) {
            Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
            exceptionResolver.resolveException(request, response, null, (Exception) throwable);
        }

        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ApiExceptionResponseBody.serialize(List.of(authException.getMessage()), objectMapper));
        }
    }
}
