package ltd.ligma.vorovayka.mapper;

import ltd.ligma.vorovayka.model.RefreshToken;
import ltd.ligma.vorovayka.model.User;
import ltd.ligma.vorovayka.model.payload.ClientMeta;
import org.mapstruct.Mapper;

@Mapper
public interface RefreshTokenMapper {
    RefreshToken toRefreshToken(User user, ClientMeta meta);
}
