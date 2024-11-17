package ltd.ligma.vorovayka.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.mapper.UserMapper;
import ltd.ligma.vorovayka.model.dto.UserDto;
import ltd.ligma.vorovayka.model.payload.LoginPayload;
import ltd.ligma.vorovayka.model.payload.LogoutPayload;
import ltd.ligma.vorovayka.model.payload.RefreshPayload;
import ltd.ligma.vorovayka.model.payload.TokenPairPayload;
import ltd.ligma.vorovayka.security.ClientMetaProvider;
import ltd.ligma.vorovayka.service.AccessTokenService;
import ltd.ligma.vorovayka.service.AuthService;
import ltd.ligma.vorovayka.service.RefreshTokenService;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutPayload logoutPayload,
                                       @CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        authService.logout(logoutPayload, refreshToken);
        response.addCookie(accessTokenService.eraseTokenCookie());
        response.addCookie(refreshTokenService.eraseTokenCookie());
        return ResponseEntity.noContent().build();
    }
}
