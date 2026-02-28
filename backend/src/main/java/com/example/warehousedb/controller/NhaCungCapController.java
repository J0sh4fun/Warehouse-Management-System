package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.NhaCungCapRequest;
import com.example.warehousedb.dto.response.NhaCungCapResponse;
import com.example.warehousedb.service.NhaCungCapService;
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

@Tag(name = "Nha Cung Cap", description = "Quan ly nha cung cap")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/nhacungcap")
@RequiredArgsConstructor
public class NhaCungCapController {

    private final NhaCungCapService nhaCungCapService;

    @Operation(summary = "Lay tat ca nha cung cap")
    @GetMapping
    public ResponseEntity<List<NhaCungCapResponse>> getAll() {
        return ResponseEntity.ok(nhaCungCapService.getAllNhaCungCap());
    }

    @Operation(summary = "Lay chi tiet 1 nha cung cap")
    @GetMapping("/{maNCC}")
    public ResponseEntity<NhaCungCapResponse> getById(@PathVariable Integer maNCC) {
        return ResponseEntity.ok(nhaCungCapService.getNhaCungCapById(maNCC));
    }

    @Operation(summary = "[ADMIN] Tao nha cung cap moi")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NhaCungCapResponse> create(@Valid @RequestBody NhaCungCapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nhaCungCapService.taoNhaCungCap(request));
    }

    @Operation(summary = "[ADMIN] Cap nhat nha cung cap")
    @PutMapping("/{maNCC}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NhaCungCapResponse> update(@PathVariable Integer maNCC, @Valid @RequestBody NhaCungCapRequest request) {
        return ResponseEntity.ok(nhaCungCapService.updateNhaCungCap(maNCC, request));
    }

    @Operation(summary = "[ADMIN] Xoa nha cung cap")
    @DeleteMapping("/{maNCC}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer maNCC) {
        nhaCungCapService.deleteNhaCungCap(maNCC);
        return ResponseEntity.noContent().build();
    }
}

