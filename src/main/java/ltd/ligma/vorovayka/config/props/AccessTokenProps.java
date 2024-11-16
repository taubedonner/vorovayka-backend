package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt.access-token")
public record AccessTokenProps(String privateKeyFile, Long expiresIn, String authoritiesClaim) {}
