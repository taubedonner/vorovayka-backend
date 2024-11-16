package ltd.ligma.vorovayka.model.payload;

import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;

import java.util.UUID;

public record OrderStateProjection(UUID id, OrderStateEnum state) {
}
