package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security.cors")
public record CorsConfigProps(List<String> allowedOrigins, List<String> allowedHeaders,
                              List<String> allowedMethods, Boolean allowCredentials, Long maxAge) {
}
