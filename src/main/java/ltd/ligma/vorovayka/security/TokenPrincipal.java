package ltd.ligma.vorovayka.security;

import java.util.UUID;

public record TokenPrincipal(UUID userId, UUID sessionId) {
}
