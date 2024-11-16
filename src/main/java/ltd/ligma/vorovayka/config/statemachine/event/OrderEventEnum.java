package ltd.ligma.vorovayka.config.statemachine.event;

public enum OrderEventEnum {
    RESERVE,
    CANCEL_RESERVE,
    PURCHASE,
    CANCEL_PURCHASE,
    REFUND,
    TRANSIT,
    EDIT_RESERVED,
    COMPLETE,
    EXPIRE_RESERVE,
    LEAVE_FEEDBACK,
    CANCEL_DELIVERY,
    CANCEL_PICKUP,
    DELIVER
}
