package ltd.ligma.vorovayka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.exception.TokenFormatException;
import ltd.ligma.vorovayka.exception.UserAuthException;
import ltd.ligma.vorovayka.model.RefreshToken;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.model.payload.ClientMeta;
import ltd.ligma.vorovayka.model.payload.LoginPayload;
import ltd.ligma.vorovayka.model.payload.RefreshPayload;
import ltd.ligma.vorovayka.model.payload.TokenPairPayload;
import ltd.ligma.vorovayka.security.SecurityContextHolderWrapper;
import ltd.ligma.vorovayka.security.TokenPrincipal;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextHolderWrapper securityContextHolder;


    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user);
    }

    public TokenPairPayload login(LoginPayload loginPayload, ClientMeta clientMeta) {
        Authentication authentication = authenticate(loginPayload).orElseThrow(UserAuthException::new);
        securityContextHolder.setAuthentication(authentication);
        return generateTokenPair(authentication, clientMeta).orElseThrow(() -> new UserAuthException("Token pair creation exception"));
    }

    public TokenPairPayload refresh(RefreshPayload refreshPayload, @Nullable String refreshCookie, ClientMeta clientMeta) {
        String refreshTokenFromReq = refreshCookie;

        if (!StringUtils.hasText(refreshTokenFromReq)) {
            if (StringUtils.hasText(refreshPayload.getRefreshToken())) {
                refreshTokenFromReq = refreshPayload.getRefreshToken();
            } else {
                throw new TokenFormatException("Refresh token was not provided");
            }
        }

        RefreshToken refreshToken = refreshTokenService.validateAndRefresh(refreshTokenFromReq, clientMeta);
        String accessToken = accessTokenService.generateAccessToken(refreshToken.getUser(), refreshToken.getId());
        return new TokenPairPayload(accessToken, refreshToken.getToken());
    }

    public void logout(TokenPrincipal principal) {
        terminateSession(principal, principal.sessionId());
    }

    public void terminateSession(TokenPrincipal principal, UUID sessionId) {
        refreshTokenService.terminateUserSession(principal.userId(), sessionId);
    }

    public List<RefreshToken> getSessions(TokenPrincipal principal) {
        return refreshTokenService.findTokensByUserId(principal.userId()) ;
    }

    public Optional<Authentication> authenticate(LoginPayload loginPayload) {
        try {
            return Optional.ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginPayload.getEmail(), loginPayload.getPassword())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<TokenPairPayload> generateTokenPair(Authentication authentication, ClientMeta clientMeta) {
        var payload = new TokenPairPayload();
        var user = (User) authentication.getPrincipal();
        var refreshToken = refreshTokenService.create(user, clientMeta);
        payload.setAccessToken(accessTokenService.generateAccessToken(user, refreshToken.getId()));
        payload.setRefreshToken(refreshToken.getToken());
        return Optional.of(payload);
    }
}
