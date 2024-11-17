package ltd.ligma.vorovayka.mapper;

import ltd.ligma.vorovayka.model.Order;
import ltd.ligma.vorovayka.model.dto.OrderDto;
import ltd.ligma.vorovayka.model.dto.ReserveOrderDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(uses = {UserMapper.class})
public interface OrderMapper {
    Order toOrder(ReserveOrderDto reserveOrderDto);

    OrderDto toOrderDto(Order order);

    default Page<OrderDto> toOrderDtoPage(Page<Order> orderPage) {
        return orderPage.map(this::toOrderDto);
    }
}
