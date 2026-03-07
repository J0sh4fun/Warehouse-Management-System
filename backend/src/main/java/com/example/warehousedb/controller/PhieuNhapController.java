package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.PhieuNhapRequest;
import com.example.warehousedb.dto.response.PhieuNhapResponse;
import com.example.warehousedb.service.PhieuNhapService;
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

@Tag(name = "Phieu Nhap", description = "Quan ly phieu nhap hang")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/phieunhap")
@RequiredArgsConstructor
public class PhieuNhapController {

    private final PhieuNhapService phieuNhapService;

    @Operation(summary = "[ADMIN] Lay tat ca phieu nhap")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PhieuNhapResponse>> getAll() {
        return ResponseEntity.ok(phieuNhapService.getAllPhieuNhap());
    }

    @Operation(summary = "Lay phieu nhap theo kho")
    @GetMapping("/kho/{maKho}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<PhieuNhapResponse>> getByKho(@PathVariable Integer maKho) {
        return ResponseEntity.ok(phieuNhapService.getPhieuNhapByKho(maKho));
    }

    @Operation(summary = "Lay phieu nhap theo nha cung cap")
    @GetMapping("/nhacungcap/{maNCC}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PhieuNhapResponse>> getByNhaCungCap(@PathVariable Integer maNCC) {
        return ResponseEntity.ok(phieuNhapService.getPhieuNhapByNhaCungCap(maNCC));
    }

    @Operation(summary = "Lay chi tiet 1 phieu nhap")
    @GetMapping("/{maPhieuNhap}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PhieuNhapResponse> getById(@PathVariable Integer maPhieuNhap) {
        return ResponseEntity.ok(phieuNhapService.getPhieuNhapById(maPhieuNhap));
    }

    @Operation(summary = "[CUSTOMER] Tao phieu nhap moi")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PhieuNhapResponse> create(@Valid @RequestBody PhieuNhapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phieuNhapService.taoPhieuNhap(request));
    }

    @Operation(summary = "[ADMIN] Xoa phieu nhap")
    @DeleteMapping("/{maPhieuNhap}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer maPhieuNhap) {
        phieuNhapService.deletePhieuNhap(maPhieuNhap);
        return ResponseEntity.noContent().build();
    }
}

