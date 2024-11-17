package ltd.ligma.vorovayka.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.service.AccessTokenService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final SecurityContextHolderWrapper securityContextHolder;

    private final AccessTokenService accessTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        securityContextHolder.setAuthentication(accessTokenService.getAccessTokenAuthenticationFromRequest(request));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        try {
            var handlerMapping = requestMappingHandlerMapping.getHandler(request);
            if (handlerMapping == null) throw new RuntimeException("Here's a bug"); // FIXME: Handle this shit properly
            var handler = (HandlerMethod) handlerMapping.getHandler();
            var method = handler.getMethod();
            var skip = !method.isAnnotationPresent(IsUser.class) && !method.isAnnotationPresent(IsAdmin.class);
            log.debug("Captured request: IsUser={}, IsAdmin{}, Skip={}", method.isAnnotationPresent(IsUser.class), method.isAnnotationPresent(IsAdmin.class), skip);
            return skip;
        } catch (Exception e) {
            return true;
        }
    }
}
