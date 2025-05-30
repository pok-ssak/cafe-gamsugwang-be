package pokssak.gsg.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import pokssak.gsg.common.filter.JwtAuthenticationFilter;
import pokssak.gsg.common.filter.JwtExceptionFilter;
import pokssak.gsg.common.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
        "/api/v1/auth/login",
        "/api/v1/auth/signup",
        "/api/v1/auth/oauth/kakao",
        "/api/v1/auth/oauth/naver",
        "api/v1/auth/oauth/google",
        "/api/v1/cafes/**",
        "/batch/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST).permitAll()
                .anyRequest().authenticated()
            )

            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            .httpBasic(HttpBasicConfigurer::disable)

            .csrf(CsrfConfigurer::disable)

            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtExceptionFilter(), UsernamePasswordAuthenticationFilter.class)

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
