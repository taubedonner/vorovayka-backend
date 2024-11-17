package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(Role.Names name);

    Set<Role> findByNameIn(Set<Role.Names> names);
}
