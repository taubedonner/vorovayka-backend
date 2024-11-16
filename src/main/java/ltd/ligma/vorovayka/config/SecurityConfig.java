package ltd.ligma.vorovayka.config;

import lombok.RequiredArgsConstructor;
import ltd.ligma.vorovayka.config.props.CorsConfigProps;
import ltd.ligma.vorovayka.security.AuthenticationEntryPointImpl;
import ltd.ligma.vorovayka.security.JwtAuthFilter;
import ltd.ligma.vorovayka.security.JwtExceptionFilter;
import ltd.ligma.vorovayka.service.impl.UserDetailsServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Profile("!dev")
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsConfigProps.class)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfig {
    private final CorsConfigProps corsConfigProps;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationEntryPointImpl exAuthEntryPoint;

    private final JwtAuthFilter jwtAuthFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authManagerBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer docsCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/docs/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize.requestMatchers("/**")
                .authenticated()
                .anyRequest()
                .permitAll());
        //http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);
        http.exceptionHandling(exh -> exh.authenticationEntryPoint(exAuthEntryPoint));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsConfigProps.allowedOrigins());
        configuration.setAllowedHeaders(corsConfigProps.allowedHeaders());
        configuration.setAllowedMethods(corsConfigProps.allowedMethods());
        configuration.setAllowCredentials(corsConfigProps.allowCredentials());
        configuration.setMaxAge(corsConfigProps.maxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
