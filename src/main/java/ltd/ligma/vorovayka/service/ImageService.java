package ltd.ligma.vorovayka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.ligma.vorovayka.config.props.MediaConfigProps;
import ltd.ligma.vorovayka.config.props.PathsConfigProps;
import ltd.ligma.vorovayka.exception.*;
import org.imgscalr.Scalr;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({MediaConfigProps.class, PathsConfigProps.class})
public class ImageService {
    private final MediaConfigProps mediaProps;
    private final PathsConfigProps pathsProps;

    public void saveImage(String prefix, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("An empty file was provided");
        }

        List<String> supportedTypes = List.of(ImageIO.getReaderMIMETypes());
        String providedType = file.getContentType();
        if (!supportedTypes.contains(providedType)) {
            throw new UnsupportedMediaTypeException(String.format("File with type '%s' is not supported. Supported types: %s",
                    providedType, supportedTypes));
        }

        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new UnprocessableEntityException("Could not read provided file");
        }

        saveOriginalImage(originalImage, prefix);
        saveThumbnailImages(originalImage, prefix);
    }

    public ByteArrayInputStream readOriginalImage(String prefix) {
        return readImage(getDir(mediaProps.images().basePath()), prefix);
    }

    public ByteArrayInputStream readThumbnailImage(String prefix, String tag) { // TODO: asdfasf
        return readImage(getDir(mediaProps.thumbnails().basePath()), taggedPrefix(prefix, tag));
    }

    public void deleteOriginalImage(String prefix) {
        try {
            Files.deleteIfExists(getFinalDir(mediaProps.images().basePath(), prefix));
        } catch (Exception e) {
            log.error(String.format("Could not delete image with prefix '%s'", prefix), e);
        }
    }

    public void deleteThumbnailImages(String prefix) {
        try {
            var files = getDir(mediaProps.thumbnails().basePath()).toFile().listFiles(((dir, name) -> name.startsWith(prefix)));
            if (files == null || files.length == 0) return;
            Arrays.stream(files).forEach(File::delete);
        } catch (Exception e) {
            log.error(String.format("Could not delete thumbnails with prefix '%s'", prefix), e);
        }
    }

    private void saveOriginalImage(BufferedImage originalImage, String prefix) {
        Integer minWidth = mediaProps.images().minWidth();
        Integer minHeight = mediaProps.images().minHeight();

        if (originalImage.getWidth() < minWidth || originalImage.getHeight() < minHeight) {
            throw new BadRequestException(String.format("Min image dimensions are %dx%d", minWidth, minHeight));
        }

        try {
            ImageIO.write(originalImage, mediaProps.images().format(), recreateFile(mediaProps.images().basePath(), prefix));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Failed to save image");
        }
    }

    private void saveThumbnailImages(BufferedImage originalImage, String prefix) {
        mediaProps.thumbnails().dimensions().forEach(thumb -> {
            var thumbnailImage = switch (thumb.mode()) {
                case FIT -> Scalr.resize(originalImage, thumb.w(), thumb.h());
                case FILL -> resizeAndCrop(originalImage, thumb.w(), thumb.h());
            };
            try {
                ImageIO.write(thumbnailImage, mediaProps.images().format(), recreateFile(mediaProps.thumbnails().basePath(), taggedPrefix(prefix, thumb.tag())));
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalServerException("Failed to save thumbnail");
            }
        });
    }

    private File recreateFile(String imageTypeDir, String prefix) {
        try {
            Path dir = Files.createDirectories(getDir(imageTypeDir));
            Files.deleteIfExists(dir.resolve(prefix));
            return Files.createFile(dir.resolve(prefix)).toFile();
        } catch (Exception e) {
            throw new InternalServerException("Failed to create file in FS");
        }
    }

    private ByteArrayInputStream readImage(Path dir, String prefix) {
        Path path = dir.resolve(prefix);
        if (Files.notExists(path)) {
            throw new NotFoundException(String.format("Image file with prefix '%s' not found", prefix));
        }
        try {
            return new ByteArrayInputStream(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new InternalServerException("Failed to read binary data");
        }
    }

    private Path getDir(String imageTypeDir) {
        return Path.of(pathsProps.staticFiles(), mediaProps.basePath(), imageTypeDir);
    }

    private Path getFinalDir(String imageTypeDir, String prefix) {
        return getDir(imageTypeDir).resolve(prefix);
    }

    private static BufferedImage resizeAndCrop(BufferedImage sourceImage, int targetWidth, int targetHeight) {
        double targetAspect = (double) targetWidth / targetHeight;
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        double sourceAspect = (double) sourceWidth / sourceHeight;

        BufferedImage croppedImage;

        if (sourceAspect > targetAspect) {
            int newWidth = (int) (sourceHeight * targetAspect);
            int xOffset = (sourceWidth - newWidth) / 2;
            croppedImage = Scalr.crop(sourceImage, xOffset, 0, newWidth, sourceHeight);
        } else {
            int newHeight = (int) (sourceWidth / targetAspect);
            int yOffset = (sourceHeight - newHeight) / 2;
            croppedImage = Scalr.crop(sourceImage, 0, yOffset, sourceWidth, newHeight);
        }

        return Scalr.resize(croppedImage, Scalr.Method.QUALITY, targetWidth, targetHeight);
    }

    private static String taggedPrefix(String prefix, String tag) {
        return String.format("%s_%s", prefix, tag);
    }
}
