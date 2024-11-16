package ltd.ligma.vorovayka.mapper;

import ltd.ligma.vorovayka.model.Order;
import ltd.ligma.vorovayka.model.dto.OrderDto;
import ltd.ligma.vorovayka.model.dto.ReserveOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {UserMapper.class})
public interface OrderMapper {
    Order toOrder(ReserveOrderDto reserveOrderDto);

    OrderDto toOrderDto(Order order);
}
