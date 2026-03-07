package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.SanPhamRequest;
import com.example.warehousedb.dto.response.SanPhamResponse;
import com.example.warehousedb.entity.DanhMuc;
import com.example.warehousedb.entity.KhachHang;
import com.example.warehousedb.entity.SanPham;
import com.example.warehousedb.repository.DanhMucRepository;
import com.example.warehousedb.repository.KhachHangRepository;
import com.example.warehousedb.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SanPhamService {

    private final SanPhamRepository    sanPhamRepository;
    private final DanhMucRepository    danhMucRepository;
    private final KhachHangRepository  khachHangRepository;

    // ── TAO SAN PHAM ──────────────────────────────────────────────────────
    public SanPhamResponse taoSanPham(SanPhamRequest request) {
        DanhMuc danhMuc = danhMucRepository.findById(request.getMaDanhMuc())
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + request.getMaDanhMuc()));

        KhachHang khachHang = khachHangRepository.findById(request.getMaKhachHang())
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + request.getMaKhachHang()));

        SanPham sanPham = SanPham.builder()
                .tenSanPham(request.getTenSanPham())
                .giaBan(request.getGiaBan())
                .danhMuc(danhMuc)
                .khachHang(khachHang)
                .build();
        sanPhamRepository.save(sanPham);
        return toResponse(sanPham);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<SanPhamResponse> getAllSanPham() {
        return sanPhamRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY DANH MUC ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<SanPhamResponse> getSanPhamByDanhMuc(Integer maDanhMuc) {
        danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + maDanhMuc));
        return sanPhamRepository.findByDanhMuc_MaDanhMuc(maDanhMuc).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY KHACH HANG ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<SanPhamResponse> getSanPhamByKhachHang(Integer maKhachHang) {
        khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + maKhachHang));
        return sanPhamRepository.findByKhachHang_MaKhachHang(maKhachHang).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public SanPhamResponse getSanPhamById(Integer maSanPham) {
        return toResponse(sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + maSanPham)));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    public SanPhamResponse updateSanPham(Integer maSanPham, SanPhamRequest request) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + maSanPham));

        DanhMuc danhMuc = danhMucRepository.findById(request.getMaDanhMuc())
                .orElseThrow(() -> new IllegalArgumentException("Danh muc khong ton tai: " + request.getMaDanhMuc()));

        KhachHang khachHang = khachHangRepository.findById(request.getMaKhachHang())
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + request.getMaKhachHang()));

        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setGiaBan(request.getGiaBan());
        sanPham.setDanhMuc(danhMuc);
        sanPham.setKhachHang(khachHang);
        sanPhamRepository.save(sanPham);
        return toResponse(sanPham);
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public void deleteSanPham(Integer maSanPham) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + maSanPham));

        if (!sanPham.getTonKhoList().isEmpty()) {
            throw new IllegalArgumentException("Khong the xoa san pham co ton kho");
        }

        sanPhamRepository.delete(sanPham);
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private SanPhamResponse toResponse(SanPham sanPham) {
        return SanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .giaBan(sanPham.getGiaBan())
                .maDanhMuc(sanPham.getDanhMuc().getMaDanhMuc())
                .tenDanhMuc(sanPham.getDanhMuc().getTenDanhMuc())
                .maKhachHang(sanPham.getKhachHang().getMaKhachHang())
                .tenKhachHang(sanPham.getKhachHang().getTenKhachHang())
                .build();
    }
}

