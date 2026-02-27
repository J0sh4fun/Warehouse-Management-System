package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.ThanhToanRequest;
import com.example.warehousedb.dto.response.ThanhToanResponse;
import com.example.warehousedb.service.ThanhToanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Thanh Toan", description = "Xu ly thanh toan don hang")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/thanhtoan")
@RequiredArgsConstructor
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    @Operation(summary = "Thanh toan don hang")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ThanhToanResponse> thanhToan(@Valid @RequestBody ThanhToanRequest request) {
        return ResponseEntity.ok(thanhToanService.thanhToan(request));
    }

    @Operation(summary = "Lay thong tin thanh toan cua don hang")
    @GetMapping("/donhang/{maDonHang}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ThanhToanResponse> getByDonHang(@PathVariable Integer maDonHang) {
        return ResponseEntity.ok(thanhToanService.getByDonHang(maDonHang));
    }

    @Operation(summary = "[ADMIN] Lay danh sach thanh toan theo trang thai")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ThanhToanResponse>> getByTrangThai(
            @RequestParam(defaultValue = "Paid") String trangThai) {
        return ResponseEntity.ok(thanhToanService.getByTrangThai(trangThai));
    }
}
