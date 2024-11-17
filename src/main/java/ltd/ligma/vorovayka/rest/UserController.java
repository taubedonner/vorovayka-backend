package ltd.ligma.vorovayka.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.mapper.UserMapper;
import ltd.ligma.vorovayka.model.Role;
import ltd.ligma.vorovayka.model.dto.UserDto;
import ltd.ligma.vorovayka.service.UserService;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "User Endpoints", description = "User Management API")
public class UserController {
    private final UserService userService;

    private final UserMapper userMapper;

    @IsAdmin
    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userMapper.toUserDto(userService.createUser(userMapper.toUser(userDto)));
    }

    @IsAdmin
    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @IsUser
    @Operation(summary = "Get user info by Access Token")
    @GetMapping("me")
    public UserDto findByToken(@AuthenticationPrincipal UUID id) {
        return userMapper.toUserDto(userService.findById(id));
    }

    @IsAdmin
    @GetMapping("{id}")
    public UserDto findById(@PathVariable UUID id) {
        return userMapper.toUserDto(userService.findById(id));
    }

    @IsAdmin
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @IsAdmin
    @PatchMapping("{id}")
    public UserDto editRoles(@PathVariable UUID id, @RequestBody Set<Role.Names> roleList) {
        return userMapper.toUserDto(userService.editRoles(id, roleList));
    }
}
