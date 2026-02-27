package com.example.warehousedb.auth.controller;

import com.example.warehousedb.auth.dto.AuthResponse;
import com.example.warehousedb.auth.dto.LoginRequest;
import com.example.warehousedb.auth.dto.RegisterRequest;
import com.example.warehousedb.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Đăng nhập / Đăng ký")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Đăng nhập (Admin hoặc KhachHang)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Đăng ký tài khoản Khách Hàng")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerKhachHang(request));
    }

    /**
     * Endpoint nội bộ – tạo tài khoản Admin.
     * Trong production nên bảo vệ endpoint này hoặc xoá đi sau khi seed xong.
     */
    @Operation(summary = "Tạo tài khoản Admin (nội bộ)")
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }
}

