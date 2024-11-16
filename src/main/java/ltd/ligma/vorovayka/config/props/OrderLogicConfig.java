package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "app.logic.orders")
public record OrderLogicConfig(Long expirationTime, ChronoUnit expirationTimeUnit) {
}
