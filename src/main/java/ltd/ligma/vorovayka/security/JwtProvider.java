package ltd.ligma.vorovayka.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.config.props.AccessTokenProps;
import ltd.ligma.vorovayka.exception.TokenExpiredException;
import ltd.ligma.vorovayka.exception.TokenFormatException;
import ltd.ligma.vorovayka.exception.TokenOperationException;
import ltd.ligma.vorovayka.model.User;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AccessTokenProps.class)
public class JwtProvider {
    private final AccessTokenProps accessTokenProps;

    public String generateAccessToken(User user) {
        try {
            return Jwts.builder()
                    .subject(user.getId().toString())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusMillis(accessTokenProps.expiresIn())))
                    .claim(accessTokenProps.authoritiesClaim(), user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .signWith(getAccessTokenSecret())
                    .compact();
        } catch (Exception e) {
            log.error("Access Token creation Exception: {}", e.getMessage());
            e.printStackTrace();
            throw new TokenFormatException("Unknown access token creation error");
        }

    }

    public Claims parseAccessToken(String token) {
        try {
            return Jwts.parser().verifyWith(getAccessTokenSecret()).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Provided access token has expired");
        } catch (JwtException e) {
            throw new TokenFormatException("Provided access token is not valid");
        }
    }

    private SecretKey getAccessTokenSecret() {
        try {
            InputStreamReader reader = new InputStreamReader(new ClassPathResource(accessTokenProps.privateKeyFile()).getInputStream());
            return Keys.hmacShaKeyFor(new PemReader(reader).readPemObject().getContent());
        } catch (Exception e) {
            log.error("Failed to get secret for access token from file");
            e.printStackTrace();
            throw new TokenOperationException("Could not get secret for access token");
        }
    }

}
