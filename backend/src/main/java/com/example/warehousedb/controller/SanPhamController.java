package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.SanPhamRequest;
import com.example.warehousedb.dto.response.SanPhamResponse;
import com.example.warehousedb.service.SanPhamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "San Pham", description = "Quan ly san pham")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sanpham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @Operation(summary = "Lay tat ca san pham")
    @GetMapping
    public ResponseEntity<List<SanPhamResponse>> getAll() {
        return ResponseEntity.ok(sanPhamService.getAllSanPham());
    }

    @Operation(summary = "Lay san pham theo danh muc")
    @GetMapping("/danhmuc/{maDanhMuc}")
    public ResponseEntity<List<SanPhamResponse>> getByDanhMuc(@PathVariable Integer maDanhMuc) {
        return ResponseEntity.ok(sanPhamService.getSanPhamByDanhMuc(maDanhMuc));
    }

    @Operation(summary = "Lay san pham cua khach hang")
    @GetMapping("/khachhang/{maKhachHang}")
    public ResponseEntity<List<SanPhamResponse>> getByKhachHang(@PathVariable Integer maKhachHang) {
        return ResponseEntity.ok(sanPhamService.getSanPhamByKhachHang(maKhachHang));
    }

    @Operation(summary = "Lay chi tiet 1 san pham")
    @GetMapping("/{maSanPham}")
    public ResponseEntity<SanPhamResponse> getById(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(sanPhamService.getSanPhamById(maSanPham));
    }

    @Operation(summary = "[CUSTOMER/ADMIN] Tao san pham moi")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<SanPhamResponse> create(@Valid @RequestBody SanPhamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sanPhamService.taoSanPham(request));
    }

    @Operation(summary = "[CUSTOMER/ADMIN] Cap nhat san pham")
    @PutMapping("/{maSanPham}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<SanPhamResponse> update(@PathVariable Integer maSanPham, @Valid @RequestBody SanPhamRequest request) {
        return ResponseEntity.ok(sanPhamService.updateSanPham(maSanPham, request));
    }

    @Operation(summary = "[ADMIN] Xoa san pham")
    @DeleteMapping("/{maSanPham}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer maSanPham) {
        sanPhamService.deleteSanPham(maSanPham);
        return ResponseEntity.noContent().build();
    }
}

