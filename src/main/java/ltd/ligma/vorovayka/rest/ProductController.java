package ltd.ligma.vorovayka.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.filter.ProductFilter;
import ltd.ligma.vorovayka.model.Product;
import ltd.ligma.vorovayka.model.dto.SaveProductDto;
import ltd.ligma.vorovayka.service.ProductService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product Endpoints")
@RequestMapping("products")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @IsAdmin
    @DocumentedOperation(desc = "Create new product", errors = HttpStatus.BAD_REQUEST)
    @PostMapping
    public Product createProduct(@RequestBody @Valid SaveProductDto productDto) {
        return productService.create(productDto);
    }

    @DocumentedOperation(desc = "Output page with products", errors = HttpStatus.BAD_REQUEST)
    @GetMapping
    public PagedModel<Product> findProducts(@ParameterObject ProductFilter specification, @ParameterObject Pageable pageable) {
        return new PagedModel<>(productService.findAll(pageable, specification));
    }

    @DocumentedOperation(desc = "Find product by ID", errors = HttpStatus.NOT_FOUND)
    @GetMapping("{id}")
    public Product findProductById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    @IsAdmin
    @DocumentedOperation(desc = "Update product info by ID", errors = {HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST})
    @PutMapping("{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody @Valid SaveProductDto productDto) {
        return productService.update(id, productDto);
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Delete product with specified ID", errors = HttpStatus.BAD_REQUEST)
    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
    }
}
