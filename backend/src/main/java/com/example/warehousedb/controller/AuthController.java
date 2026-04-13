package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.LoginRequest;
import com.example.warehousedb.dto.request.RegisterRequest;
import com.example.warehousedb.dto.response.AuthResponse;
import com.example.warehousedb.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Dang nhap / Dang ky")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Dang nhap (Admin hoac KhachHang)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Dang ky tai khoan Khach Hang")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerKhachHang(request));
    }

    @Operation(summary = "Tao tai khoan Admin (noi bo / seed)")
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }
}
