package com.example.warehousedb.config;

import com.example.warehousedb.security.JwtAuthEntryPoint;
import com.example.warehousedb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api-docs/**",
            "/api-docs",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**"
    };

    /** Shared provider — not a @Bean so Spring Security auto-config won't trigger the warning */
    private DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Tat anonymous filter: request khong co token se bi coi la "chua xac thuc"
                // va duoc chuyen thang toi JwtAuthEntryPoint (tra 401) thay vi 403 mac dinh
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // 401 — chua dang nhap / token sai / khong co token
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        // 403 — da dang nhap nhung role khong du quyen
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authenticationProvider(daoAuthProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthProvider());
    }

    /** Tra ve JSON 403 khi da xac thuc nhung khong du quyen */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var body = Map.of(
                    "status", 403,
                    "error", "Forbidden",
                    "message", "Ban khong co quyen truy cap endpoint nay. " +
                               "Role hien tai cua ban khong phu hop (can ADMIN hoac CUSTOMER tuong ung).",
                    "path", request.getRequestURI()
            );
            new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getOutputStream(), body);
            response.getOutputStream().flush();
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
