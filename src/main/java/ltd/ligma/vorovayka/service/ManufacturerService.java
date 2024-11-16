package ltd.ligma.vorovayka.service;

import ltd.ligma.vorovayka.exception.BadRequestException;
import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.model.Manufacturer;
import ltd.ligma.vorovayka.model.dto.SaveNamedDto;
import ltd.ligma.vorovayka.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    public Manufacturer create(SaveNamedDto dto) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(dto.getName());
        return manufacturerRepository.save(manufacturer);
    }

    public List<Manufacturer> findAll() {
        return manufacturerRepository.findAll();
    }

    public Manufacturer findById(UUID id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Manufacturer with ID %s not found", id)));
    }

    public Manufacturer update(UUID id, SaveNamedDto dto) {
        try {
            Manufacturer manufacturer = findById(id);
            manufacturer.setName(dto.getName());
            return manufacturerRepository.save(manufacturer);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getResponseBody().getDetails());
        }
    }
    
    public void delete(UUID id) {
        manufacturerRepository.deleteById(id);
    }
}
