package ltd.ligma.vorovayka.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.mapper.RefreshTokenMapper;
import ltd.ligma.vorovayka.mapper.UserMapper;
import ltd.ligma.vorovayka.model.dto.SessionDto;
import ltd.ligma.vorovayka.model.dto.UserDto;
import ltd.ligma.vorovayka.model.payload.LoginPayload;
import ltd.ligma.vorovayka.model.payload.RefreshPayload;
import ltd.ligma.vorovayka.model.payload.TokenPairPayload;
import ltd.ligma.vorovayka.security.ClientMetaProvider;
import ltd.ligma.vorovayka.security.TokenPrincipal;
import ltd.ligma.vorovayka.service.AccessTokenService;
import ltd.ligma.vorovayka.service.AuthService;
import ltd.ligma.vorovayka.service.RefreshTokenService;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "User Authentication API")
public class AuthController {
    private final AuthService authService;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final ClientMetaProvider clientMetaProvider;

    private final UserMapper userMapper;

    private final RefreshTokenMapper refreshTokenMapper;

    @PostMapping("register")
    @Operation(summary = "Register new user and get its info")
    public UserDto register(@RequestBody @Valid UserDto userDto) {
        return userMapper.toUserDto(authService.register(userMapper.toUser(userDto)));
    }

    @PostMapping("login")
    @Operation(summary = "Check user credentials and generate token pair")
    public TokenPairPayload login(@RequestBody @Valid LoginPayload loginPayload, HttpServletRequest request, HttpServletResponse response) {
        TokenPairPayload pair = authService.login(loginPayload, clientMetaProvider.retrieveClientMeta(request));
        response.addCookie(accessTokenService.createTokenCookie(pair.getAccessToken()));
        response.addCookie(refreshTokenService.createTokenCookie(pair.getRefreshToken()));
        return pair;
    }

    @PostMapping("refresh")
    @Operation(summary = "Refresh token pair by Refresh Token")
    public TokenPairPayload login(@RequestBody @Valid RefreshPayload refreshPayload, HttpServletRequest request, HttpServletResponse response,
                                  @CookieValue(value = "refresh_token", required = false) String refreshTokenCookie) {
        TokenPairPayload pair = authService.refresh(refreshPayload, refreshTokenCookie, clientMetaProvider.retrieveClientMeta(request));
        response.addCookie(accessTokenService.createTokenCookie(pair.getAccessToken()));
        response.addCookie(refreshTokenService.createTokenCookie(pair.getRefreshToken()));
        return pair;
    }

    @IsUser
    @PostMapping("logout")
    @Operation(summary = "Clear refresh cookie, delete device related refresh token, invalidate access token")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal TokenPrincipal tokenPrincipal, HttpServletResponse response) {
        authService.logout(tokenPrincipal);
        response.addCookie(accessTokenService.eraseTokenCookie());
        response.addCookie(refreshTokenService.eraseTokenCookie());
        return ResponseEntity.noContent().build();
    }

    @IsUser
    @GetMapping("sessions")
    public List<SessionDto> getSessions(@AuthenticationPrincipal TokenPrincipal tokenPrincipal) {
        var sessions = refreshTokenMapper.toSessionDtoList(authService.getSessions(tokenPrincipal));
        sessions.stream().filter(s -> s.getId() == tokenPrincipal.sessionId()).findFirst().ifPresent(s -> s.setIsActive(true));
        return sessions;
    }

    @IsUser
    @PostMapping("sessions/terminate")
    @Operation(summary = "Terminate session on specific device by its ID")
    public ResponseEntity<Void> terminate(@RequestParam UUID sessionId, @AuthenticationPrincipal TokenPrincipal tokenPrincipal) {
        authService.terminateSession(tokenPrincipal, sessionId);
        return ResponseEntity.noContent().build();
    }
}
