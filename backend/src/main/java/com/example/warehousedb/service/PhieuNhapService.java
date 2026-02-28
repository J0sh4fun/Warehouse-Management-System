package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.PhieuNhapRequest;
import com.example.warehousedb.dto.response.PhieuNhapResponse;
import com.example.warehousedb.entity.*;
import com.example.warehousedb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhieuNhapService {

    private final PhieuNhapRepository         phieuNhapRepository;
    private final KhoRepository               khoRepository;
    private final NhaCungCapRepository        nhaCungCapRepository;
    private final SanPhamRepository           sanPhamRepository;
    private final TonKhoRepository            tonKhoRepository;

    // ── TAO PHIEU NHAP ────────────────────────────────────────────────────
    @Transactional
    public PhieuNhapResponse taoPhieuNhap(PhieuNhapRequest request) {
        Kho kho = khoRepository.findById(request.getMaKho())
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + request.getMaKho()));

        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(request.getMaNCC())
                .orElseThrow(() -> new IllegalArgumentException("Nha cung cap khong ton tai: " + request.getMaNCC()));

        PhieuNhap phieuNhap = PhieuNhap.builder()
                .ngayNhap(LocalDate.now())
                .kho(kho)
                .nhaCungCap(nhaCungCap)
                .build();
        phieuNhapRepository.save(phieuNhap);

        // Tao chi tiet — trigger DB se tang ton kho tu dong
        List<ChiTietPhieuNhap> chiTietList = request.getChiTiet().stream().map(ct -> {
            SanPham sanPham = sanPhamRepository.findById(ct.getMaSanPham())
                    .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + ct.getMaSanPham()));

            return ChiTietPhieuNhap.builder()
                    .id(new ChiTietPhieuNhapId(phieuNhap.getMaPhieuNhap(), sanPham.getMaSanPham()))
                    .phieuNhap(phieuNhap)
                    .sanPham(sanPham)
                    .soLuong(ct.getSoLuong())
                    .giaNhap(ct.getGiaNhap())
                    .build();
        }).collect(Collectors.toList());

        phieuNhap.setChiTietList(chiTietList);
        // Persist details via cascade and flush so DB trigger updates TonKho immediately.
        phieuNhapRepository.saveAndFlush(phieuNhap);
        return toResponse(phieuNhap);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<PhieuNhapResponse> getAllPhieuNhap() {
        return phieuNhapRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY KHO ────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<PhieuNhapResponse> getPhieuNhapByKho(Integer maKho) {
        khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + maKho));
        return phieuNhapRepository.findByKho_MaKho(maKho).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY NHA CUNG CAP ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<PhieuNhapResponse> getPhieuNhapByNhaCungCap(Integer maNCC) {
        nhaCungCapRepository.findById(maNCC)
                .orElseThrow(() -> new IllegalArgumentException("Nha cung cap khong ton tai: " + maNCC));
        return phieuNhapRepository.findByNhaCungCap_MaNCC(maNCC).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public PhieuNhapResponse getPhieuNhapById(Integer maPhieuNhap) {
        return toResponse(phieuNhapRepository.findById(maPhieuNhap)
                .orElseThrow(() -> new IllegalArgumentException("Phieu nhap khong ton tai: " + maPhieuNhap)));
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public void deletePhieuNhap(Integer maPhieuNhap) {
        PhieuNhap phieuNhap = phieuNhapRepository.findById(maPhieuNhap)
                .orElseThrow(() -> new IllegalArgumentException("Phieu nhap khong ton tai: " + maPhieuNhap));

        // Roll back stock before removing import details.
        Integer maKho = phieuNhap.getKho().getMaKho();
        phieuNhap.getChiTietList().forEach(ct -> {
            TonKhoId tonKhoId = new TonKhoId(maKho, ct.getSanPham().getMaSanPham());
            TonKho tonKho = tonKhoRepository.findById(tonKhoId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Ton kho khong ton tai - Kho: " + maKho
                            + ", San pham: " + ct.getSanPham().getMaSanPham()));

            int soLuongMoi = tonKho.getSoLuong() - ct.getSoLuong();
            if (soLuongMoi < 0) {
                throw new IllegalStateException(
                        "Du lieu ton kho khong hop le khi xoa phieu nhap " + maPhieuNhap
                        + " (so luong ton se am)");
            }

            tonKho.setSoLuong(soLuongMoi);
            tonKhoRepository.save(tonKho);
        });

        phieuNhapRepository.delete(phieuNhap);
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private PhieuNhapResponse toResponse(PhieuNhap phieuNhap) {
        BigDecimal tongTien = phieuNhap.getChiTietList().stream()
                .map(ct -> ct.getGiaNhap().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PhieuNhapResponse.builder()
                .maPhieuNhap(phieuNhap.getMaPhieuNhap())
                .ngayNhap(phieuNhap.getNgayNhap())
                .maKho(phieuNhap.getKho().getMaKho())
                .tenKho(phieuNhap.getKho().getTenKho())
                .maNCC(phieuNhap.getNhaCungCap().getMaNCC())
                .tenNCC(phieuNhap.getNhaCungCap().getTenNCC())
                .soChiTiet(phieuNhap.getChiTietList().size())
                .tongTien(tongTien)
                .build();
    }
}

