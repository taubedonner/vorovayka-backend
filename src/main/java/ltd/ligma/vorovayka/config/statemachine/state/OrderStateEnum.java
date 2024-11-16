package ltd.ligma.vorovayka.config.statemachine.state;

public enum OrderStateEnum {
    RAW,
    RESERVED,
    RESERVE_CANCELLED,
    PURCHASED,
    PURCHASE_CANCELLED,
    IN_TRANSIT,
    READY_FOR_PICKUP,
    COMPLETED,
    REFUNDED,
    RESERVE_EXPIRED,
}
