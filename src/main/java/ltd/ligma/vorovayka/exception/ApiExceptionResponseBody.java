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

    public static String serialize(List<String> details, ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(new ApiExceptionResponseBody(details));
    }

    public static String serialize(ApiException ex, ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(ex.getResponseBody());
    }
}
