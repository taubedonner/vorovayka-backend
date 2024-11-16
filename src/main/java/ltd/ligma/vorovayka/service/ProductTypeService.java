package ltd.ligma.vorovayka.service;

import ltd.ligma.vorovayka.exception.BadRequestException;
import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.model.ProductType;
import ltd.ligma.vorovayka.model.dto.SaveNamedDto;
import ltd.ligma.vorovayka.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;

    public ProductType create(SaveNamedDto dto) {
        ProductType type = new ProductType();
        type.setName(dto.getName());
        return productTypeRepository.save(type);
    }

    public List<ProductType> findAll() {
        return productTypeRepository.findAll();
    }

    public ProductType findById(UUID id) {
        return productTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product type with ID %s not found", id)));
    }

    public ProductType update(UUID id, SaveNamedDto dto) {
        try {
            ProductType productType = findById(id);
            productType.setName(dto.getName());
            return productTypeRepository.save(productType);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getResponseBody().getDetails());
        }
    }

    public void delete(UUID id) {
        productTypeRepository.deleteById(id);
    }
}
