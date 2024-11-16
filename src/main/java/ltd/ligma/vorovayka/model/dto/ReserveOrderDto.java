package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ReserveOrderDto {
    @NotNull
    @Length(min = 10, max = 255)
    private String address;

    @Valid
    @NotEmpty
    private Set<SaveOrderProductDto> products = new HashSet<>();
}
