package ltd.ligma.vorovayka.config;

import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GeneratorsConfig {
    @Bean
    public ULID ulid() {
        return new ULID();
    }
}
