package ltd.ligma.vorovayka.service;

import ltd.ligma.vorovayka.config.props.MediaConfigProps;
import ltd.ligma.vorovayka.config.props.PathsConfigProps;
import ltd.ligma.vorovayka.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Integer minWidth = mediaProps.getImages().getMinWidth();
        Integer minHeight = mediaProps.getImages().getMinHeight();
        if (originalImage.getWidth() < minWidth || originalImage.getHeight() < minHeight) {
            throw new BadRequestException(String.format("Min image dimensions are %dx%d", minWidth, minHeight));
        }

        try {
            Integer thumbWidth = mediaProps.getThumbnails().getWidth();
            Integer thumbHeight = mediaProps.getThumbnails().getHeight();
            BufferedImage thumbnailImage = Scalr.resize(originalImage, thumbWidth, thumbHeight);
            ImageIO.write(originalImage, mediaProps.getImages().getFormat(), recreateFile(mediaProps.getImages().getBasePath(), prefix));
            ImageIO.write(thumbnailImage, mediaProps.getImages().getFormat(), recreateFile(mediaProps.getThumbnails().getBasePath(), prefix));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Failed to save image");
        }
    }

    public ByteArrayInputStream readOriginalImage(String prefix) {
        return readImage(getDir(mediaProps.getImages().getBasePath()), prefix);
    }

    public ByteArrayInputStream readThumbnailImage(String prefix) {
        return readImage(getDir(mediaProps.getThumbnails().getBasePath()), prefix);
    }

    public void deleteOriginalImage(String prefix) {
        try {
            Files.deleteIfExists(getFinalDir(mediaProps.getImages().getBasePath(), prefix));
        } catch (Exception e) {
            log.error(String.format("Could not delete image with prefix '%s'", prefix), e);
        }
    }

    public void deleteThumbnailImage(String prefix) {
        try {
            Files.deleteIfExists(getFinalDir(mediaProps.getThumbnails().getBasePath(), prefix));
        } catch (Exception e) {
            log.error(String.format("Could not delete thumbnail with prefix '%s'", prefix), e);
        }
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
        return Path.of(pathsProps.staticFiles(), mediaProps.getBasePath(), imageTypeDir);
    }

    private Path getFinalDir(String imageTypeDir, String prefix) {
        return getDir(imageTypeDir).resolve(prefix);
    }
}
