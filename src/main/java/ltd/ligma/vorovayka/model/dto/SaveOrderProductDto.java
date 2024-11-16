package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
public class SaveOrderProductDto {
    @NotNull
    private UUID productId;

    @Positive
    private BigInteger quantity = BigInteger.ONE;
}
