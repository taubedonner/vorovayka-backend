package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.paths")
public record PathsConfigProps(String staticFiles, String logFiles) {
}
