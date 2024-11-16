package ltd.ligma.vorovayka.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.exception.ApiException;
import ltd.ligma.vorovayka.exception.ApiExceptionResponseBody;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Order(2)
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApiException e) {
            log.debug("Api Exception Filter: {}", e.getMessage());
            response.setStatus(e.getClass().getAnnotation(ResponseStatus.class).value().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ApiExceptionResponseBody.serialize(List.of(e.getMessage(), request.getRequestURI())));
        } catch (RuntimeException e) {
            log.warn("Unhandled Exception: {}", e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ApiExceptionResponseBody.serialize(List.of(e.getMessage())));
        }
    }
}
