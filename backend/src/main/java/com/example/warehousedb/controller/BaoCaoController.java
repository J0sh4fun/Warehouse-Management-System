package com.example.warehousedb.controller;

import com.example.warehousedb.dto.response.BaoCaoDoanhThuResponse;
import com.example.warehousedb.dto.response.DonHangResponse;
import com.example.warehousedb.service.BaoCaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Bao Cao", description = "Bao cao doanh thu va thong ke ban hang")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/baocao")
@RequiredArgsConstructor
public class BaoCaoController {

    private final BaoCaoService baoCaoService;

    @Operation(summary = "[ADMIN] Doanh thu theo tung thang")
    @GetMapping("/doanhthu/thang")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BaoCaoDoanhThuResponse>> doanhThuTheoThang() {
        return ResponseEntity.ok(baoCaoService.getDoanhThuTheoThang());
    }

    @Operation(summary = "[ADMIN] Don hang theo khoang thoi gian")
    @GetMapping("/donhang")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DonHangResponse>> donHangTheoKhoang(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(baoCaoService.getDonHangTheoKhoang(tuNgay, denNgay));
    }
}
