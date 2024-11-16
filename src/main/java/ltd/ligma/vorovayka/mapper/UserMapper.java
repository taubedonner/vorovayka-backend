package ltd.ligma.vorovayka.mapper;

import ltd.ligma.vorovayka.model.Role;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "roles", ignore = true)
    User toUser(UserDto userDto);

    default List<String> map(Set<Role> value) {
        return value.stream().map(role -> role.getName().toString()).collect(Collectors.toList());
    }
}
