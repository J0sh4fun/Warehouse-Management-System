package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.LoginRequest;
import com.example.warehousedb.dto.request.RegisterRequest;
import com.example.warehousedb.dto.response.AuthResponse;
import com.example.warehousedb.entity.Admin;
import com.example.warehousedb.entity.KhachHang;
import com.example.warehousedb.repository.AdminRepository;
import com.example.warehousedb.repository.KhachHangRepository;
import com.example.warehousedb.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final KhachHangRepository khachHangRepository;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMatKhau())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        if ("ROLE_ADMIN".equals(role)) {
            Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow();
            return AuthResponse.builder()
                    .accessToken(token).tokenType("Bearer").role("ADMIN")
                    .email(admin.getEmail()).ten(admin.getTenAdmin()).id(admin.getMaAdmin())
                    .build();
        } else {
            KhachHang kh = khachHangRepository.findByEmail(request.getEmail()).orElseThrow();
            return AuthResponse.builder()
                    .accessToken(token).tokenType("Bearer").role("CUSTOMER")
                    .email(kh.getEmail()).ten(kh.getTenKhachHang()).id(kh.getMaKhachHang())
                    .build();
        }
    }

    // ── REGISTER KHACH HANG ───────────────────────────────────────────────────
    @Transactional
    public AuthResponse registerKhachHang(RegisterRequest request) {
        if (khachHangRepository.existsByEmail(request.getEmail()) ||
                adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email da duoc su dung: " + request.getEmail());
        }
        KhachHang kh = KhachHang.builder()
                .tenKhachHang(request.getTen()).email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .dienThoai(request.getDienThoai()).diaChi(request.getDiaChi())
                .build();
        khachHangRepository.save(kh);
        String token = jwtTokenProvider.generateToken(kh.getEmail());
        return AuthResponse.builder()
                .accessToken(token).tokenType("Bearer").role("CUSTOMER")
                .email(kh.getEmail()).ten(kh.getTenKhachHang()).id(kh.getMaKhachHang())
                .build();
    }

    // ── REGISTER ADMIN ────────────────────────────────────────────────────────
    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email da duoc su dung: " + request.getEmail());
        }
        Admin admin = Admin.builder()
                .tenAdmin(request.getTen()).email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .build();
        adminRepository.save(admin);
        String token = jwtTokenProvider.generateToken(admin.getEmail());
        return AuthResponse.builder()
                .accessToken(token).tokenType("Bearer").role("ADMIN")
                .email(admin.getEmail()).ten(admin.getTenAdmin()).id(admin.getMaAdmin())
                .build();
    }
}
