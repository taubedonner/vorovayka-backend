package ltd.ligma.vorovayka.rest;

import ltd.ligma.vorovayka.model.Manufacturer;
import ltd.ligma.vorovayka.model.dto.SaveNamedDto;
import ltd.ligma.vorovayka.service.ManufacturerService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Manufacturer Endpoints")
@RequestMapping("manufacturers")
@RestController
@RequiredArgsConstructor
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @IsAdmin
    @DocumentedOperation(desc = "Create new manufacturer", errors = HttpStatus.BAD_REQUEST)
    @PostMapping
    public Manufacturer createProduct(@Valid @RequestBody SaveNamedDto manufacturerDto) {
        return manufacturerService.create(manufacturerDto);
    }

    @DocumentedOperation(desc = "Retrieve list with manufacturers", errors = HttpStatus.BAD_REQUEST)
    @GetMapping
    public List<Manufacturer> findProducts() {
        return manufacturerService.findAll();
    }

    @DocumentedOperation(desc = "Find manufacturer by ID", errors = HttpStatus.NOT_FOUND)
    @GetMapping("{id}")
    public Manufacturer findProductById(@PathVariable UUID id) {
        return manufacturerService.findById(id);
    }

    @IsAdmin
    @DocumentedOperation(desc = "Update manufacturer name by ID", errors = {HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST})
    @PutMapping("{id}")
    public Manufacturer update(@PathVariable UUID id, @Valid @RequestBody SaveNamedDto manufacturerDto) {
        return manufacturerService.update(id, manufacturerDto);
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DocumentedOperation(desc = "Delete manufacturer by ID", errors = HttpStatus.BAD_REQUEST)
    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable UUID id) {
        manufacturerService.delete(id);
    }
}
