package ltd.ligma.vorovayka.service;

import de.huxhorn.sulky.ulid.ULID;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@EnableConfigurationProperties(RefreshTokenProps.class)
public class RefreshTokenService {
    private final RefreshTokenProps props;

    private final RefreshTokenRepository repository;

    private final RefreshTokenMapper mapper;

    private final ULID ulid;

    public RefreshToken create(User user, ClientMeta meta) {
        var token = generateToken(user, meta);

        // Terminate previous sessions on specific (same) device
        //repository.deleteByUserIdAndFingerprint(user.getId(), token.getFingerprint());

        return repository.save(token);
    }

    public void deleteByToken(String token) {
        try {
            repository.deleteByToken(token);
        } catch (EmptyResultDataAccessException e) {
            throw new RefreshTokenMisuseException("Refresh token has already been removed");
        }
    }

    public void terminateUserSession(UUID userId, UUID sessionId) {
        try {
            repository.deleteByIdAndUserId(sessionId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new RefreshTokenMisuseException("Session has already been terminated");
        }
    }

    public List<RefreshToken> findTokensByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public RefreshToken validateAndRefresh(String token, ClientMeta meta) {
        var fingerprint = ClientMetaProvider.calculateHash(meta);

        var found = repository.findValidByTokenAndFingerprint(token, fingerprint).orElseThrow(() -> new BadTokenException("Refresh token is bad or expired"));

        var newToken = generateToken(found.getUser(), meta, fingerprint);

        deleteByToken(found.getToken());

        return repository.save(newToken);
    }

    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
    }

    public Cookie createTokenCookie(String refreshToken) {
        return CookieHelper.createHttpOnlyCookie(props.cookie().key(), refreshToken, props.cookie().path(), props.expiresIn());
    }

    public Cookie eraseTokenCookie() {
        return CookieHelper.createHttpOnlyCookie(props.cookie().key(), "", props.cookie().path(), 0L);
    }

    private RefreshToken generateToken(User user, ClientMeta meta, String fingerprint) {
        var ts = LocalDateTime.now();
        var token = mapper.toRefreshToken(user, meta);
        token.setToken(ulid.nextULID(ZonedDateTime.of(ts, ZoneId.systemDefault()).toInstant().toEpochMilli()));
        token.setExpiresIn(ts.plus(props.expiresIn(), ChronoUnit.MILLIS));
        token.setFingerprint(fingerprint);
        return token;
    }

    private RefreshToken generateToken(User user, ClientMeta meta) {
        var fingerprint = ClientMetaProvider.calculateHash(meta);
        return generateToken(user, meta, fingerprint);
    }
}
