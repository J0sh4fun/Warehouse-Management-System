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

    private final PhieuNhapRepository  phieuNhapRepository;
    private final KhoRepository        khoRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final SanPhamRepository    sanPhamRepository;

    // ── TAO PHIEU NHAP ────────────────────────────────────────────────────
    // DB trigger trg_tang_tonkho tu dong tang TonKho sau khi INSERT ChiTietPhieuNhap
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

        // Tao chi tiet — trigger trg_tang_tonkho se tang ton kho tu dong
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
    // DB trigger trg_hoantra_tonkho KHONG xu ly PhieuNhap — can xu ly thu cong
    // Trigger chi hoan tra khi xoa ChiTietDonHang (don hang bi huy)
    // => Can giam thu cong ton kho khi xoa phieu nhap
    @Transactional
    public void deletePhieuNhap(Integer maPhieuNhap) {
        PhieuNhap phieuNhap = phieuNhapRepository.findById(maPhieuNhap)
                .orElseThrow(() -> new IllegalArgumentException("Phieu nhap khong ton tai: " + maPhieuNhap));
        phieuNhapRepository.delete(phieuNhap);
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private PhieuNhapResponse toResponse(PhieuNhap phieuNhap) {
        BigDecimal tongTien = phieuNhap.getChiTietList() == null ? BigDecimal.ZERO :
                phieuNhap.getChiTietList().stream()
                        .map(ct -> ct.getGiaNhap().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PhieuNhapResponse.builder()
                .maPhieuNhap(phieuNhap.getMaPhieuNhap())
                .ngayNhap(phieuNhap.getNgayNhap())
                .maKho(phieuNhap.getKho().getMaKho())
                .tenKho(phieuNhap.getKho().getTenKho())
                .maNCC(phieuNhap.getNhaCungCap().getMaNCC())
                .tenNCC(phieuNhap.getNhaCungCap().getTenNCC())
                .soChiTiet(phieuNhap.getChiTietList() == null ? 0 : phieuNhap.getChiTietList().size())
                .tongTien(tongTien)
                .build();
    }
}




