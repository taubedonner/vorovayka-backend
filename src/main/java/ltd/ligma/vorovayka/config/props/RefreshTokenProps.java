package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.security.auth.refresh-token")
public record RefreshTokenProps(Long expiresIn, @NestedConfigurationProperty CookieProps cookie) {}
