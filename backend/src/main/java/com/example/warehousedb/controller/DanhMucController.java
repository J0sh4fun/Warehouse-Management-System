package com.example.warehousedb.controller;

import com.example.warehousedb.dto.request.DanhMucRequest;
import com.example.warehousedb.dto.response.DanhMucResponse;
import com.example.warehousedb.service.DanhMucService;
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

@Tag(name = "Danh Muc", description = "Quan ly danh muc san pham")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/danhmuc")
@RequiredArgsConstructor
public class DanhMucController {

    private final DanhMucService danhMucService;

    @Operation(summary = "Lay tat ca danh muc")
    @GetMapping
    public ResponseEntity<List<DanhMucResponse>> getAll() {
        return ResponseEntity.ok(danhMucService.getAllDanhMuc());
    }

    @Operation(summary = "Lay chi tiet 1 danh muc")
    @GetMapping("/{maDanhMuc}")
    public ResponseEntity<DanhMucResponse> getById(@PathVariable Integer maDanhMuc) {
        return ResponseEntity.ok(danhMucService.getDanhMucById(maDanhMuc));
    }

    @Operation(summary = "[ADMIN] Tao danh muc moi")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DanhMucResponse> create(@Valid @RequestBody DanhMucRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(danhMucService.taoDanhMuc(request));
    }

    @Operation(summary = "[ADMIN] Cap nhat danh muc")
    @PutMapping("/{maDanhMuc}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DanhMucResponse> update(@PathVariable Integer maDanhMuc, @Valid @RequestBody DanhMucRequest request) {
        return ResponseEntity.ok(danhMucService.updateDanhMuc(maDanhMuc, request));
    }

    @Operation(summary = "[ADMIN] Xoa danh muc")
    @DeleteMapping("/{maDanhMuc}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer maDanhMuc) {
        danhMucService.deleteDanhMuc(maDanhMuc);
        return ResponseEntity.noContent().build();
    }
}

