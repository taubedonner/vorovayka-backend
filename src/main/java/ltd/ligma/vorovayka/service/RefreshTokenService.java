package ltd.ligma.vorovayka.service;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.config.props.RefreshTokenProps;
import ltd.ligma.vorovayka.exception.BadTokenException;
import ltd.ligma.vorovayka.exception.RefreshTokenMisuseException;
import ltd.ligma.vorovayka.mapper.RefreshTokenMapper;
import ltd.ligma.vorovayka.model.RefreshToken;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.model.payload.ClientMeta;
import ltd.ligma.vorovayka.repository.RefreshTokenRepository;
import ltd.ligma.vorovayka.security.ClientMetaProvider;
import ltd.ligma.vorovayka.util.CookieHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@EnableConfigurationProperties(RefreshTokenProps.class)
public class RefreshTokenService {
    private final RefreshTokenProps props;

    private final RefreshTokenRepository repository;

    private final RefreshTokenMapper mapper;

    public RefreshToken create(User user, ClientMeta meta) {
        var fingerprint = ClientMetaProvider.calculateHash(meta);

        // Terminate previous sessions on specific device
        repository.deleteByUserAndFingerprint(user, fingerprint);

        // Create token
        var token = mapper.toRefreshToken(user, meta);
        token.setFingerprint(fingerprint);
        token.setExpiresIn(LocalDateTime.now().plus(props.expiresIn(), ChronoUnit.MILLIS));

        return repository.save(token);
    }

    public void deleteByToken(String token) {
        try {
            repository.deleteById(UUID.fromString(token));
        } catch (EmptyResultDataAccessException e) {
            throw new RefreshTokenMisuseException("Refresh token has already been removed");
        }
    }

    public void deleteByToken(UUID token) {
        try {
            repository.deleteById(token);
        } catch (EmptyResultDataAccessException e) {
            throw new RefreshTokenMisuseException("Refresh token has already been removed");
        }
    }

    public RefreshToken validateAndRefresh(String token, ClientMeta meta) {
        var fingerprint = ClientMetaProvider.calculateHash(meta);

        var found = repository.findValidByTokenAndFingerprint(UUID.fromString(token), fingerprint)
                .orElseThrow(() -> new BadTokenException("Refresh token is bad or expired"));

        var newToken = create(found.getUser(), meta);
        deleteByToken(found.getToken());

        return newToken;
    }

    public void deleteAllByUserId(UUID userId) {
        repository.deleteAllByUserId(userId);
    }

    public Cookie createTokenCookie(String refreshToken) {
        return CookieHelper.createHttpOnlyCookie(props.cookie().key(), refreshToken, props.cookie().path(), props.expiresIn());
    }

    public Cookie eraseTokenCookie() {
        return CookieHelper.createHttpOnlyCookie(props.cookie().key(), "", props.cookie().path(), 0L);
    }
}
