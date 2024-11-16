package ltd.ligma.vorovayka.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import ltd.ligma.vorovayka.util.json.InstantDeserializer;
import ltd.ligma.vorovayka.util.json.InstantSerializer;

import java.time.Instant;

@Getter
@Setter
public class AuditableDto {
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonProperty(value = "updatedAt", access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;
}
