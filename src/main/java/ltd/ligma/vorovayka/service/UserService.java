package ltd.ligma.vorovayka.service;

import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.exception.BadRequestException;
import ltd.ligma.vorovayka.exception.ChildNotFoundException;
import ltd.ligma.vorovayka.exception.NotFoundException;
import ltd.ligma.vorovayka.model.Role;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.repository.RoleRepository;
import ltd.ligma.vorovayka.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RefreshTokenService refreshTokenService;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Cannot create user with provided info");
        }

        User newUser = userRepository.save(user);
        newUser.getRoles().add(roleRepository.findByName(Role.Names.ROLE_USER)
                .orElseThrow(() -> new ChildNotFoundException(Role.class, Role.Names.ROLE_USER.toString())));
        return newUser;
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(User.class, email));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(UUID id) {
        refreshTokenService.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public User editRoles(UUID id, Set<Role.Names> roleList) {
        var user = findById(id);
        var foundRoles = roleRepository.findByNameIn(roleList);
        if (foundRoles.size() != roleList.size()) throw new NotFoundException("Some of provided roles were not found in database");
        user.setRoles(foundRoles);
        userRepository.save(user);
        return user;
    }
}
