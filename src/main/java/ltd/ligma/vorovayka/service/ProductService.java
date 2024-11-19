package ltd.ligma.vorovayka.service;

import ltd.ligma.vorovayka.exception.ApiException;
import ltd.ligma.vorovayka.exception.BadRequestException;
import ltd.ligma.vorovayka.exception.InternalServerException;
import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.model.Manufacturer;
import ltd.ligma.vorovayka.model.Product;
import ltd.ligma.vorovayka.model.ProductType;
import ltd.ligma.vorovayka.model.dto.SaveProductDto;
import ltd.ligma.vorovayka.model.payload.ProductMeta;
import ltd.ligma.vorovayka.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTypeService productTypeService;
    private final ManufacturerService manufacturerService;
    private final OrderProductService orderProductService;
    private final ImageService imageService;

    public Product create(SaveProductDto productDto) {
        Product product = new Product();
        mutate(product, productDto);
        return productRepository.save(product);
    }

    public Page<Product> findAll(Pageable pageable, Specification<Product> specification) {
        Page<Product> products = productRepository.findAll(specification, pageable);
        products.get().forEach(this::setMeta);
        return products;
    }

    public Product findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product with ID %s not found", id)));
        setMeta(product);
        return product;
    }

    public Product update(UUID id, SaveProductDto productDto) {
        try {
            Product product = findById(id);
            mutate(product, productDto);
            return productRepository.save(product);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getResponseBody().getDetails());
        }
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
        deleteImage(id);
    }

    public void deleteImage(UUID id) {
        String prefix = generatePrefix(id);
        imageService.deleteOriginalImage(prefix);
        imageService.deleteThumbnailImages(prefix);
    }

    private void mutate(Product product, SaveProductDto productDto) {
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setType(retrieveProductType(productDto.getTypeId()));
        product.setManufacturer(retrieveManufacturer(productDto.getManufacturerId()));
    }

    private void setMeta(Product product) {
        ProductMeta meta = orderProductService.loadProductMeta(product.getId());
        product.setOrderCount(meta.orderCount());
        product.setRating(meta.rating());
    }

    private ProductType retrieveProductType(UUID id) {
        try {
            return productTypeService.findById(id);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getResponseBody().getDetails());
        }
    }

    private Manufacturer retrieveManufacturer(UUID id) {
        try {
            return manufacturerService.findById(id);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getResponseBody().getDetails());
        }
    }

    public void saveImage(UUID id, MultipartFile file) {
        if (productRepository.existsById(id)) {
            imageService.saveImage(generatePrefix(id), file);
        } else {
            throw new BadRequestException(String.format("Failed to attach an image to a non-existent product (%s)", id));
        }
    }

    public byte[] readOriginalImage(UUID id) {
        try {
            return imageService.readOriginalImage(generatePrefix(id)).readAllBytes();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Failed to read bytes from input stream");
        }
    }

    public byte[] readThumbnailImage(UUID id, String tag) {
        try {
            return imageService.readThumbnailImage(generatePrefix(id), tag).readAllBytes();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Failed to read bytes from input stream");
        }
    }

    private String generatePrefix(UUID id) {
        return "product_" + id.toString();
    }
}
