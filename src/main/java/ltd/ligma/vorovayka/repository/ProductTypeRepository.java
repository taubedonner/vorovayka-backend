package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
}
