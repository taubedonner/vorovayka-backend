package ltd.ligma.vorovayka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.media")
public record MediaConfigProps(String basePath, Images images, Thumbnails thumbnails) {
    public record Images(String format, String basePath, Integer minWidth, Integer minHeight) {
    }

    public record Thumbnails(String format, String basePath, List<ThumbnailDimensions> dimensions) {
    }

    public record ThumbnailDimensions(String tag, Integer w, Integer h, ThumbnailScaleMode mode) {
    }

    public enum ThumbnailScaleMode {
        FILL,
        FIT
    }
}
