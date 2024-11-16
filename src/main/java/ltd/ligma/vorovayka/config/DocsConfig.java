package ltd.ligma.vorovayka.config;

import ltd.ligma.vorovayka.exception.ApiExceptionResponseBody;
import ltd.ligma.vorovayka.model.Role;
import ltd.ligma.vorovayka.util.annotations.security.IsAdmin;
import ltd.ligma.vorovayka.util.annotations.security.IsUser;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedBearerToken;
import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedOperation;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DocsConfig {
    public static final String BEARER_TOKEN_SCHEME = "bearer-token";
    @Value("${app.docs.title:Vorovayka Store API Docs}")
    private String apiTitle;

    @Value("${app.docs.version:v1.0}")
    private String apiVersion;

    @Bean
    public GlobalOpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.setInfo(new Info().title(apiTitle).version(apiVersion));
            openApi.getComponents().addSecuritySchemes(BEARER_TOKEN_SCHEME, new SecurityScheme()
                    .scheme("bearer").description("Put access token only").type(SecurityScheme.Type.HTTP).bearerFormat("JWT"));
        };
    }

    /* TODO: Use these annotations to set correct time format "HH:mm"
      @Schema(
          type = "string",
          example = "20230203",
          pattern = "yyyyMMdd"
      )
      @JsonFormat(pattern="yyyyMMdd")
      LocalDate myDate
     */

    @Bean
    public GlobalOperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            interceptGlobal(operation);
            interceptDocumentedBearerToken(operation, handlerMethod);
            interceptDocumentedOperation(operation, handlerMethod);
            interceptRole(IsAdmin.class, Role.Names.ROLE_ADMIN.toString(), operation, handlerMethod);
            interceptRole(IsUser.class, Role.Names.ROLE_USER.toString(), operation, handlerMethod);
            return operation;
        };
    }

    private void interceptGlobal(Operation operation) {
        addErrorResponse(operation, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void interceptDocumentedBearerToken(Operation operation, HandlerMethod handlerMethod) {
        boolean methodLevel = handlerMethod.hasMethodAnnotation(DocumentedBearerToken.class);
        boolean controllerLevel = handlerMethod.getMethod().getDeclaringClass()
                .isAnnotationPresent(DocumentedBearerToken.class);
        if (controllerLevel || methodLevel) {
            addErrorResponse(operation, HttpStatus.UNAUTHORIZED, null);
            addErrorResponse(operation, HttpStatus.FORBIDDEN, null);
            operation.addSecurityItem(new SecurityRequirement().addList(BEARER_TOKEN_SCHEME));
        }
    }

    private void interceptDocumentedOperation(Operation operation, HandlerMethod handlerMethod) {
        DocumentedOperation annotation = handlerMethod.getMethodAnnotation(DocumentedOperation.class);
        if (annotation != null) {
            Arrays.stream(annotation.errors()).forEach(s -> addErrorResponse(operation, s));
            if (!annotation.desc().isBlank()) {
                operation.setSummary(annotation.desc());
            }
        }
    }

    private <T extends Annotation> void interceptRole(Class<T> annotationType, String roleName, Operation operation, HandlerMethod handlerMethod) {
        T annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (annotation != null) {
            String desc = operation.getDescription();
            operation.setDescription(String.format("<b>[%s]</b>%s", roleName,
                    (desc == null || desc.isBlank() ? "" : " " + desc)));
        }
    }

    private void addErrorResponse(Operation operation, HttpStatus status) {
        addErrorResponse(operation, status, ApiExceptionResponseBody.class);
    }

    private void addErrorResponse(Operation operation, HttpStatus status, Class<?> body) {
        if (!status.isError()) {
            return;
        }
        ApiResponse response = new ApiResponse();
        if (body != null) {
            ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(new AnnotatedType(body).resolveAsRef(false));
            Content content = new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                    new MediaType().schema(resolvedSchema.schema));
            response.setContent(content);
        }
        response.setDescription(status.getReasonPhrase());
        operation.getResponses().addApiResponse(Integer.toString(status.value()), response);
    }
}
