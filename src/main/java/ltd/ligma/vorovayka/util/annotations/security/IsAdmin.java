package ltd.ligma.vorovayka.util.annotations.security;

import ltd.ligma.vorovayka.util.annotations.swagger.DocumentedBearerToken;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@DocumentedBearerToken
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and hasRole(@commonSecurityConfig.roles.admin())")
public @interface IsAdmin {
}
