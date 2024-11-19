package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.security.auth.access-token")
public record AccessTokenProps(String privateKeyFile, Long expiresIn, String authoritiesClaim, String sessionIdClaim, @NestedConfigurationProperty CookieProps cookie) {}
