package ltd.ligma.vorovayka.config.statemachine;

import ltd.ligma.vorovayka.config.statemachine.event.OrderEventEnum;
import ltd.ligma.vorovayka.config.statemachine.listener.OrderStateMachineListener;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "order_state_machine")
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStateEnum, OrderEventEnum> {
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStateEnum, OrderEventEnum> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(new OrderStateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStateEnum, OrderEventEnum> states) throws Exception {
        states
                .withStates()
                .initial(OrderStateEnum.RAW)
                .end(OrderStateEnum.RESERVE_CANCELLED)
                .end(OrderStateEnum.RESERVE_EXPIRED)
                .end(OrderStateEnum.REFUNDED)
                .states(EnumSet.allOf(OrderStateEnum.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStateEnum, OrderEventEnum> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStateEnum.RAW).target(OrderStateEnum.RESERVED).event(OrderEventEnum.RESERVE)
                .and().withExternal()
                .source(OrderStateEnum.RESERVED).target(OrderStateEnum.RESERVED).event(OrderEventEnum.EDIT_RESERVED)
                .and().withExternal()
                .source(OrderStateEnum.RESERVED).target(OrderStateEnum.RESERVE_CANCELLED).event(OrderEventEnum.CANCEL_RESERVE)
                .and().withExternal()
                .source(OrderStateEnum.RESERVED).target(OrderStateEnum.RESERVE_EXPIRED).event(OrderEventEnum.EXPIRE_RESERVE)
                .and().withExternal()
                .source(OrderStateEnum.RESERVED).target(OrderStateEnum.PURCHASED).event(OrderEventEnum.PURCHASE)
                .and().withExternal()
                .source(OrderStateEnum.PURCHASED).target(OrderStateEnum.PURCHASE_CANCELLED).event(OrderEventEnum.CANCEL_PURCHASE)
                .and().withExternal()
                .source(OrderStateEnum.PURCHASED).target(OrderStateEnum.IN_TRANSIT).event(OrderEventEnum.TRANSIT)
                .and().withExternal()
                .source(OrderStateEnum.IN_TRANSIT).target(OrderStateEnum.READY_FOR_PICKUP).event(OrderEventEnum.DELIVER)
                .and().withExternal()
                .source(OrderStateEnum.PURCHASE_CANCELLED).target(OrderStateEnum.REFUNDED).event(OrderEventEnum.REFUND)
                .and().withExternal()
                .source(OrderStateEnum.IN_TRANSIT).target(OrderStateEnum.PURCHASE_CANCELLED).event(OrderEventEnum.CANCEL_DELIVERY)
                .and().withExternal()
                .source(OrderStateEnum.READY_FOR_PICKUP).target(OrderStateEnum.PURCHASE_CANCELLED).event(OrderEventEnum.CANCEL_PICKUP)
                .and().withExternal()
                .source(OrderStateEnum.READY_FOR_PICKUP).target(OrderStateEnum.COMPLETED).event(OrderEventEnum.COMPLETE)
                .and().withExternal()
                .source(OrderStateEnum.COMPLETED).target(OrderStateEnum.COMPLETED).event(OrderEventEnum.LEAVE_FEEDBACK);
    }

}
