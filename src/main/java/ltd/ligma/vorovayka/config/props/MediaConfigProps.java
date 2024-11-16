package ltd.ligma.vorovayka.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.media")
public class MediaConfigProps {
    String basePath;
    Images images;
    Thumbnails thumbnails;

    @Getter
    @Setter
    public static class Images {
        String format;
        String basePath;
        Integer minWidth;
        Integer minHeight;
    }

    @Getter
    @Setter
    public static class Thumbnails {
        String format;
        String basePath;
        Integer width;
        Integer height;
    }
}
