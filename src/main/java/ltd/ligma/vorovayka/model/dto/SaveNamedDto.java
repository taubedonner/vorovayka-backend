package ltd.ligma.vorovayka.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SaveNamedDto {
    @NotNull
    @Length(min = 2, max = 60)
    private String name;
}
