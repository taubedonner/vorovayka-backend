package ltd.ligma.vorovayka.config.statemachine.listener;

import ltd.ligma.vorovayka.config.statemachine.event.OrderEventEnum;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@Slf4j
public class OrderStateMachineListener extends StateMachineListenerAdapter<OrderStateEnum, OrderEventEnum> {
    @Override
    public void eventNotAccepted(Message<OrderEventEnum> message) {
        log.error("Could not perform event: {}", message);
    }

    @Override
    public void stateMachineError(StateMachine<OrderStateEnum, OrderEventEnum> stateMachine, Exception e) {
        log.error("Order state machine error at state '{}': {}", stateMachine.getState().getId(), e.getMessage());
    }
}
