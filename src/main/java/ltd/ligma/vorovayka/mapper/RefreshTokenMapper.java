package ltd.ligma.vorovayka.mapper;

import ltd.ligma.vorovayka.model.RefreshToken;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.model.dto.SessionDto;
import ltd.ligma.vorovayka.model.payload.ClientMeta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RefreshTokenMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "user")
    RefreshToken toRefreshToken(User user, ClientMeta meta);

    SessionDto toSessionDto(RefreshToken refreshToken);

    List<SessionDto> toSessionDtoList(List<RefreshToken> refreshTokenList);
}
