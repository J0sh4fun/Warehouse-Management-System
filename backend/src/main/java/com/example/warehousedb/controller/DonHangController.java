package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.DonHangRequest;
import com.example.warehousedb.dto.response.DonHangResponse;
import com.example.warehousedb.service.DonHangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Don Hang", description = "Quan ly don hang ban hang")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/donhang")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    @Operation(summary = "[ADMIN] Lay tat ca don hang")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DonHangResponse>> getAll() {
        return ResponseEntity.ok(donHangService.getAllDonHang());
    }

    @Operation(summary = "Lay don hang theo khach hang")
    @GetMapping("/khachhang/{maKhachHang}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<DonHangResponse>> getByKhachHang(@PathVariable Integer maKhachHang) {
        return ResponseEntity.ok(donHangService.getDonHangByKhachHang(maKhachHang));
    }

    @Operation(summary = "Lay chi tiet 1 don hang")
    @GetMapping("/{maDonHang}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<DonHangResponse> getById(@PathVariable Integer maDonHang) {
        return ResponseEntity.ok(donHangService.getDonHangById(maDonHang));
    }

    @Operation(summary = "Tao don hang moi")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<DonHangResponse> tao(@Valid @RequestBody DonHangRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donHangService.taoDonHang(request));
    }

    @Operation(summary = "[ADMIN] Cap nhat trang thai don hang")
    @PatchMapping("/{maDonHang}/trangthai")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonHangResponse> capNhatTrangThai(
            @PathVariable Integer maDonHang, @RequestParam String trangThai) {
        return ResponseEntity.ok(donHangService.capNhatTrangThai(maDonHang, trangThai));
    }

    @Operation(summary = "Huy don hang (hoan tra ton kho)")
    @PatchMapping("/{maDonHang}/huy")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<DonHangResponse> huy(@PathVariable Integer maDonHang) {
        return ResponseEntity.ok(donHangService.huyDonHang(maDonHang));
    }
}
