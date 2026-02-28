package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.DanhMucRequest;
import com.example.warehousedb.dto.response.DanhMucResponse;
import com.example.warehousedb.entity.DanhMuc;
import com.example.warehousedb.repository.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;

    // ── TAO DANH MUC ──────────────────────────────────────────────────────
    public DanhMucResponse taoDanhMuc(DanhMucRequest request) {
        if (danhMucRepository.existsByTenDanhMuc(request.getTenDanhMuc())) {
            throw new IllegalArgumentException("Danh muc '" + request.getTenDanhMuc() + "' da ton tai");
        }

        DanhMuc danhMuc = DanhMuc.builder()
                .tenDanhMuc(request.getTenDanhMuc())
                .build();
        danhMucRepository.save(danhMuc);
        return toResponse(danhMuc);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DanhMucResponse> getAllDanhMuc() {
        return danhMucRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public DanhMucResponse getDanhMucById(Integer maDanhMuc) {
        return toResponse(danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + maDanhMuc)));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    public DanhMucResponse updateDanhMuc(Integer maDanhMuc, DanhMucRequest request) {
        DanhMuc danhMuc = danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + maDanhMuc));

        // Check ten trung lap neu khac
        if (!danhMuc.getTenDanhMuc().equals(request.getTenDanhMuc())
                && danhMucRepository.existsByTenDanhMuc(request.getTenDanhMuc())) {
            throw new IllegalArgumentException("Danh muc '" + request.getTenDanhMuc() + "' da ton tai");
        }

        danhMuc.setTenDanhMuc(request.getTenDanhMuc());
        danhMucRepository.save(danhMuc);
        return toResponse(danhMuc);
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public void deleteDanhMuc(Integer maDanhMuc) {
        DanhMuc danhMuc = danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + maDanhMuc));

        if (!danhMuc.getSanPhamList().isEmpty()) {
            throw new IllegalArgumentException("Khong the xoa danh muc co san pham");
        }

        danhMucRepository.delete(danhMuc);
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private DanhMucResponse toResponse(DanhMuc danhMuc) {
        return DanhMucResponse.builder()
                .maDanhMuc(danhMuc.getMaDanhMuc())
                .tenDanhMuc(danhMuc.getTenDanhMuc())
                .soSanPham(danhMuc.getSanPhamList() != null ? danhMuc.getSanPhamList().size() : 0)
                .build();
    }
}

