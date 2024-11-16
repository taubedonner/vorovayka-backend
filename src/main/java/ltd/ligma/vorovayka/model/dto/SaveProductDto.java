package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SaveProductDto {
    @NotNull
    @Length(min = 2, max = 120)
    private String name;

    @NotNull
    private UUID typeId;

    @NotNull
    private UUID manufacturerId;

    @Positive
    private BigDecimal price;

    @Length(min = 5)
    private String description;
}
