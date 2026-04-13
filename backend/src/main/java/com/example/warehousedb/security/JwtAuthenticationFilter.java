package com.example.warehousedb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        log.debug("JWT Filter - URI: {}, Token present: {}", request.getRequestURI(), token != null);

        if (StringUtils.hasText(token)) {
            try {
                boolean valid = jwtTokenProvider.validateToken(token);
                log.debug("JWT Filter - Token valid: {}", valid);

                if (valid) {
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    log.debug("JWT Filter - Username from token: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT Filter - Authentication set for: {}, roles: {}",
                            username, userDetails.getAuthorities());
                } else {
                    // Token present but invalid — return 401 immediately
                    write401(response, request.getRequestURI(),
                            "Token JWT khong hop le. Hay dang nhap lai de lay token moi.");
                    return;
                }
            } catch (Exception e) {
                log.error("JWT Filter - Error processing token: {}", e.getMessage());
                write401(response, request.getRequestURI(),
                        "Token JWT bi loi: " + e.getMessage() +
                        ". Hay dang nhap lai tai POST /api/auth/login.");
                return;
            }
        } else {
            log.debug("JWT Filter - No Bearer token for URI: {}", request.getRequestURI());
            // No token — let Spring Security handle it (will invoke JwtAuthEntryPoint for protected paths)
        }

        filterChain.doFilter(request, response);
    }

    private void write401(HttpServletResponse response, String path, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", message,
                "path", path
        );
        objectMapper.writeValue(response.getOutputStream(), body);
        response.getOutputStream().flush();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)) return null;

        bearerToken = bearerToken.trim();

        // Strip "Bearer " prefix (case-insensitive, handles double "Bearer Bearer ..." from Swagger)
        while (bearerToken.toLowerCase().startsWith("bearer ")) {
            bearerToken = bearerToken.substring(7).trim();
        }

        return StringUtils.hasText(bearerToken) ? bearerToken : null;
    }
}

