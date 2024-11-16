package ltd.ligma.vorovayka.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.config.props.AccessTokenProps;
import ltd.ligma.vorovayka.exception.TokenFormatException;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.security.JwtProvider;
import ltd.ligma.vorovayka.security.SecurityContextHolderWrapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AccessTokenProps.class)
public class AccessTokenService {
    private final JwtProvider jwtProvider;

    private final AccessTokenProps accessTokenProps;

    private final SecurityContextHolderWrapper securityContextHolder;

    /**
     * Generates JWT string from logged-in User details
     *
     * @param userDetails full user details retrieved by login
     * @return JWT string
     */
    public String generateAccessToken(User userDetails) {
        return jwtProvider.generateAccessToken(userDetails);
    }

    /**
     * Retrieves Access Token from request headers and converts it to user <u>brief</u>
     *
     * @param request request with Access Token in Authorization header or HttpOnly Cookie
     * @return user brief (id, jwt, roles)
     */
    public UsernamePasswordAuthenticationToken getAccessTokenAuthenticationFromRequest(HttpServletRequest request) {
        String accessTokenHeaderKey = "Authorization";
        String accessTokenPrefix = "Bearer ";
        String accessTokenCookie = "access_token";

        String token;

        Cookie tokenCookie = WebUtils.getCookie(request, accessTokenCookie);

        if (tokenCookie != null) {
            token = tokenCookie.getValue();
        } else {
            token = request.getHeader(accessTokenHeaderKey);

            if (!StringUtils.hasText(token)) {
                throw new TokenFormatException("Required access token is missing in request header");
            }

            if (!token.startsWith(accessTokenPrefix)) {
                throw new TokenFormatException("Unsupported access token type");
            }

            token = token.replace(accessTokenPrefix, "");
        }

//        if (tokenIsBlacklisted(token)) {
//            throw new BadTokenException("Specified access token belongs to the logged out user");
//        }

        Claims claims = jwtProvider.parseAccessToken(token);
        UUID userId = UUID.fromString(claims.getSubject());

//        if (userIsBlacklisted(userId)) {
//            throw new BadTokenException("Specified access token belongs to changed or blocked user");
//        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(accessTokenProps.authoritiesClaim())
                .toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    /**
     * Retrieves user brief from SecurityContextHolder
     *
     * @return user brief (id, jwt, roles)
     * @see AccessTokenService#getAccessTokenAuthenticationFromRequest(HttpServletRequest request)
     */
    public UsernamePasswordAuthenticationToken getAccessTokenPayloadFromContext() {
        return (UsernamePasswordAuthenticationToken) securityContextHolder.getAuthentication();
    }
}
