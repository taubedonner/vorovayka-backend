package ltd.ligma.vorovayka.service;

import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.security.ClientMetaProvider;
import ltd.ligma.vorovayka.security.SecurityContextHolderWrapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextHolderWrapper securityContextHolder;

    private final ClientMetaProvider clientMetaProvider;
}
