package com.example.warehousedb.service;

import com.example.warehousedb.dto.response.TonKhoResponse;
import com.example.warehousedb.entity.Kho;
import com.example.warehousedb.entity.SanPham;
import com.example.warehousedb.entity.TonKho;
import com.example.warehousedb.entity.TonKhoId;
import com.example.warehousedb.repository.KhoRepository;
import com.example.warehousedb.repository.SanPhamRepository;
import com.example.warehousedb.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TonKhoService {

    private final TonKhoRepository  tonKhoRepository;
    private final KhoRepository     khoRepository;
    private final SanPhamRepository sanPhamRepository;

    // ── GET ALL TON KHO ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<TonKhoResponse> getAllTonKho() {
        return tonKhoRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET TON KHO BY KHO ────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<TonKhoResponse> getTonKhoByKho(Integer maKho) {
        Kho kho = khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + maKho));
        return tonKhoRepository.findByKho_MaKho(maKho).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET TON KHO BY SAN PHAM ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<TonKhoResponse> getTonKhoBySanPham(Integer maSanPham) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + maSanPham));
        return tonKhoRepository.findBySanPham_MaSanPham(maSanPham).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET TON KHO BY KHO & SAN PHAM ─────────────────────────────────────
    @Transactional(readOnly = true)
    public TonKhoResponse getTonKhoByKhoAndSanPham(Integer maKho, Integer maSanPham) {
        TonKhoId id = new TonKhoId(maKho, maSanPham);
        TonKho tonKho = tonKhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ton kho khong ton tai - Kho: " + maKho + ", San pham: " + maSanPham));
        return toResponse(tonKho);
    }

    // ── GET SAN PHAM CO TON > 0 TRONG KHO ──────────────────────────────────
    @Transactional(readOnly = true)
    public List<TonKhoResponse> getSanPhamCoTonByKho(Integer maKho) {
        khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + maKho));
        return tonKhoRepository.findByKho_MaKhoAndSoLuongGreaterThan(maKho, 0).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET SAN PHAM SAP HET TON (< 10) TRONG KHO ──────────────────────────
    @Transactional(readOnly = true)
    public List<TonKhoResponse> getSanPhamSapHetTon(Integer maKho) {
        khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + maKho));
        return tonKhoRepository.findByKho_MaKhoAndSoLuongLessThan(maKho, 10).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── TONG SO LUONG TON TRONG KHO ────────────────────────────────────────
    @Transactional(readOnly = true)
    public Integer getTongSoLuongTonByKho(Integer maKho) {
        khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + maKho));
        List<TonKho> list = tonKhoRepository.findByKho_MaKho(maKho);
        return list.stream().mapToInt(TonKho::getSoLuong).sum();
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private TonKhoResponse toResponse(TonKho tonKho) {
        return TonKhoResponse.builder()
                .maKho(tonKho.getKho().getMaKho())
                .tenKho(tonKho.getKho().getTenKho())
                .maSanPham(tonKho.getSanPham().getMaSanPham())
                .tenSanPham(tonKho.getSanPham().getTenSanPham())
                .giaBan(tonKho.getSanPham().getGiaBan())
                .soLuong(tonKho.getSoLuong())
                .trangThaiTon(getTrangThaiTon(tonKho.getSoLuong()))
                .build();
    }

    private String getTrangThaiTon(Integer soLuong) {
        if (soLuong == 0) return "HET";
        if (soLuong < 10) return "SAP HET";
        if (soLuong < 50) return "THAP";
        return "DU";
    }
}

