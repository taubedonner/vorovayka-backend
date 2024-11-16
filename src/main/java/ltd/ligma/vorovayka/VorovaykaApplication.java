package ltd.ligma.vorovayka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EntityScan("ltd.ligma.vorovayka.model")
@SpringBootApplication
public class VorovaykaApplication {
    public static void main(String[] args) {
        SpringApplication.run(VorovaykaApplication.class, args);
    }
}
