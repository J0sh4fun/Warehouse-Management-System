package com.example.warehousedb.auth.service;

import com.example.warehousedb.entity.Admin;
import com.example.warehousedb.entity.KhachHang;
import com.example.warehousedb.repository.AdminRepository;
import com.example.warehousedb.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Tìm user theo email trong bảng Admin trước, nếu không thấy thì tìm trong KhachHang.
 * Username = email (unique across both tables is assumed).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Try Admin table first
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getMatKhau())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        // Try KhachHang table
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByEmail(email);
        if (khachHangOpt.isPresent()) {
            KhachHang kh = khachHangOpt.get();
            return User.builder()
                    .username(kh.getEmail())
                    .password(kh.getMatKhau())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                    .build();
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
    }
}

