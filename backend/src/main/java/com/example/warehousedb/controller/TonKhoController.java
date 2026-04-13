package com.example.warehousedb.controller;

import com.example.warehousedb.dto.response.TonKhoResponse;
import com.example.warehousedb.service.TonKhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ton Kho", description = "Quan ly ton kho va bao cao ton kho")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/tonkho")
@RequiredArgsConstructor
public class TonKhoController {

    private final TonKhoService tonKhoService;

    @Operation(summary = "[ADMIN] Lay tat ca ton kho")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TonKhoResponse>> getAll() {
        return ResponseEntity.ok(tonKhoService.getAllTonKho());
    }

    @Operation(summary = "Lay ton kho theo kho")
    @GetMapping("/kho/{maKho}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<TonKhoResponse>> getByKho(@PathVariable Integer maKho) {
        return ResponseEntity.ok(tonKhoService.getTonKhoByKho(maKho));
    }

    @Operation(summary = "Lay ton kho theo san pham")
    @GetMapping("/sanpham/{maSanPham}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<TonKhoResponse>> getBySanPham(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(tonKhoService.getTonKhoBySanPham(maSanPham));
    }

    @Operation(summary = "Lay ton kho theo kho va san pham")
    @GetMapping("/kho/{maKho}/sanpham/{maSanPham}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<TonKhoResponse> getByKhoAndSanPham(@PathVariable Integer maKho, @PathVariable Integer maSanPham) {
        return ResponseEntity.ok(tonKhoService.getTonKhoByKhoAndSanPham(maKho, maSanPham));
    }

    @Operation(summary = "Lay san pham co ton (> 0) trong kho")
    @GetMapping("/kho/{maKho}/coton")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<TonKhoResponse>> getSanPhamCoTon(@PathVariable Integer maKho) {
        return ResponseEntity.ok(tonKhoService.getSanPhamCoTonByKho(maKho));
    }

    @Operation(summary = "Lay san pham sap het ton (< 10) trong kho")
    @GetMapping("/kho/{maKho}/saphetton")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<TonKhoResponse>> getSanPhamSapHetTon(@PathVariable Integer maKho) {
        return ResponseEntity.ok(tonKhoService.getSanPhamSapHetTon(maKho));
    }

    @Operation(summary = "Lay tong so luong ton trong kho")
    @GetMapping("/kho/{maKho}/tong")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Integer> getTongSoLuongTon(@PathVariable Integer maKho) {
        return ResponseEntity.ok(tonKhoService.getTongSoLuongTonByKho(maKho));
    }
}

