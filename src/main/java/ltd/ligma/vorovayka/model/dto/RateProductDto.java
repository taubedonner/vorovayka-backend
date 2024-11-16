package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class RateProductDto {
    @Min(0)
    @Max(5)
    @NotNull
    private Double rate;
}
