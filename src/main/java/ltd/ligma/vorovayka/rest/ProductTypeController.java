package ltd.ligma.vorovayka.rest;

import ltd.ligma.vorovayka.model.ProductType;
import ltd.ligma.vorovayka.model.dto.SaveNamedDto;
import ltd.ligma.vorovayka.service.ProductTypeService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Product Type Endpoints")
@RequestMapping("product-types")
@RestController
@RequiredArgsConstructor
public class ProductTypeController {
    private final ProductTypeService productTypeService;

    @IsAdmin
    @DocumentedOperation(desc = "Create new product type", errors = HttpStatus.BAD_REQUEST)
    @PostMapping
    public ProductType createProduct(@RequestBody @Valid SaveNamedDto productTypeDto) {
        return productTypeService.create(productTypeDto);
    }

    @DocumentedOperation(desc = "Retrieve list with product types", errors = HttpStatus.BAD_REQUEST)
    @GetMapping
    public List<ProductType> findProducts() {
        return productTypeService.findAll();
    }

    @DocumentedOperation(desc = "Find product type by ID", errors = HttpStatus.NOT_FOUND)
    @GetMapping("{id}")
    public ProductType findProductById(@PathVariable UUID id) {
        return productTypeService.findById(id);
    }

    @IsAdmin
    @DocumentedOperation(desc = "Update product type name by ID", errors = {HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST})
    @PutMapping("{id}")
    public ProductType updateProduct(@PathVariable UUID id, @RequestBody @Valid SaveNamedDto productTypeDto) {
        return productTypeService.update(id, productTypeDto);
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Delete product type by ID", errors = HttpStatus.BAD_REQUEST)
    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable UUID id) {
        productTypeService.delete(id);
    }
}
