package ltd.ligma.vorovayka.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ApiExceptionResponseBody {
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final List<String> details;

    public static String serialize(List<String> details) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new ApiExceptionResponseBody(details));
    }
}
