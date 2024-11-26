package ltd.ligma.vorovayka.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import ltd.ligma.vorovayka.model.OrderProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderDto extends AuditableDto {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(value = "user", access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    @JsonProperty(value = "total", access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;

    @JsonProperty(value = "address", access = JsonProperty.Access.READ_ONLY)
    private String address;

    @JsonProperty(value = "state", access = JsonProperty.Access.READ_ONLY)
    private OrderStateEnum state = OrderStateEnum.RAW;

    @JsonIgnoreProperties("order")
    @JsonProperty(value = "products", access = JsonProperty.Access.READ_ONLY)
    private List<OrderProduct> products;

    @Schema(description = "Date after which order cannot be purchased")
    @JsonProperty(value = "reserveExpiresIn", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime reserveExpiresIn;
}
