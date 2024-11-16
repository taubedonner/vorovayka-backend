package ltd.ligma.vorovayka.rest;

import ltd.ligma.vorovayka.service.ProductService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Product Image Endpoints")
@RequestMapping("products/{id}/image")
public class ProductImageController {
    private final ProductService productService;

    @IsAdmin
    @DocumentedOperation(
            desc = "Set product image. Can't be null",
            errors = {HttpStatus.BAD_REQUEST, HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNPROCESSABLE_ENTITY})
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void setProductImage(@PathVariable UUID id, @io.swagger.v3.oas.annotations.parameters.RequestBody final MultipartFile image) {
        productService.saveImage(id, image);
    }

    @DocumentedOperation(desc = "Get product image by ID", errors = HttpStatus.NOT_FOUND)
    @GetMapping(value = "png/original", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getProductImage(@PathVariable UUID id) {
        return productService.readOriginalImage(id);
    }

    @DocumentedOperation(desc = "Get product thumbnail image by ID", errors = HttpStatus.NOT_FOUND)
    @GetMapping(value = "png/thumbnail", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getProductThumbnail(@PathVariable UUID id) {
        return productService.readThumbnailImage(id);
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Delete product image with specified ID", errors = HttpStatus.BAD_REQUEST)
    @DeleteMapping
    public void deleteProductImage(@PathVariable UUID id) {
        productService.deleteImage(id);
    }
}
