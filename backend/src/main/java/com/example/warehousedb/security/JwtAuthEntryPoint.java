package com.example.warehousedb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tra ve JSON 401 khi request khong co token hoac token khong hop le.
 * Thay the cho response 403 mac dinh de giup debug de hon.
 */
@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Unauthorized request to: {} - {}", request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 thay vi 403

        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", "Token JWT khong hop le hoac chua dang nhap. " +
                "Hay dang nhap tai POST /api/auth/login de lay token, " +
                "sau do click nut Authorize tren Swagger UI va nhap: Bearer <token>");
        body.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
        response.getOutputStream().flush();
    }
}

