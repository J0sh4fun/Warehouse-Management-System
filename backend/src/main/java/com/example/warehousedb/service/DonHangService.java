package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.DonHangRequest;
import com.example.warehousedb.dto.response.DonHangResponse;
import com.example.warehousedb.dto.response.ThanhToanResponse;
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
public class DonHangService {

    private final DonHangRepository        donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhoRepository            khoRepository;
    private final KhachHangRepository      khachHangRepository;
    private final SanPhamRepository        sanPhamRepository;
    private final TonKhoRepository         tonKhoRepository;
    private final ThanhToanRepository      thanhToanRepository;

    // ── TAO DON HANG ─────────────────────────────────────────────────────────
    @Transactional
    public DonHangResponse taoDonHang(DonHangRequest request) {
        Kho kho = khoRepository.findById(request.getMaKho())
                .orElseThrow(() -> new IllegalArgumentException("Kho khong ton tai: " + request.getMaKho()));
        KhachHang khachHang = khachHangRepository.findById(request.getMaKhachHang())
                .orElseThrow(() -> new IllegalArgumentException("Khach hang khong ton tai: " + request.getMaKhachHang()));

        // Validate ton kho truoc khi tao
        for (DonHangRequest.ChiTietRequest ct : request.getChiTiet()) {
            TonKhoId id = new TonKhoId(kho.getMaKho(), ct.getMaSanPham());
            TonKho tonKho = tonKhoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "San pham " + ct.getMaSanPham() + " khong co trong kho " + kho.getMaKho()));
            if (tonKho.getSoLuong() < ct.getSoLuong()) {
                throw new IllegalArgumentException(
                        "Khong du ton kho. San pham " + ct.getMaSanPham()
                        + " — ton hien tai: " + tonKho.getSoLuong()
                        + ", yeu cau: " + ct.getSoLuong());
            }
        }

        DonHang donHang = DonHang.builder()
                .ngayLap(LocalDate.now()).trangThai("Pending")
                .kho(kho).khachHang(khachHang)
                .build();
        donHangRepository.save(donHang);

        // Tao chi tiet — trigger DB se giam ton kho tu dong
        List<ChiTietDonHang> chiTietList = request.getChiTiet().stream().map(ct -> {
            SanPham sanPham = sanPhamRepository.findById(ct.getMaSanPham())
                    .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + ct.getMaSanPham()));
            return ChiTietDonHang.builder()
                    .id(new ChiTietDonHangId(donHang.getMaDonHang(), sanPham.getMaSanPham()))
                    .donHang(donHang).sanPham(sanPham)
                    .soLuong(ct.getSoLuong()).donGia(ct.getDonGia())
                    .build();
        }).collect(Collectors.toList());

        chiTietDonHangRepository.saveAll(chiTietList);
        donHang.setChiTietList(chiTietList);
        return toResponse(donHang);
    }

    // ── GET ALL (ADMIN) ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DonHangResponse> getAllDonHang() {
        return donHangRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY KHACH HANG ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DonHangResponse> getDonHangByKhachHang(Integer maKhachHang) {
        return donHangRepository.findByKhachHang_MaKhachHang(maKhachHang).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public DonHangResponse getDonHangById(Integer maDonHang) {
        return toResponse(donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Don hang khong ton tai: " + maDonHang)));
    }

    // ── CAP NHAT TRANG THAI ───────────────────────────────────────────────────
    @Transactional
    public DonHangResponse capNhatTrangThai(Integer maDonHang, String trangThai) {
        List<String> valid = List.of("Pending", "Processing", "Delivered", "Cancelled");
        if (!valid.contains(trangThai))
            throw new IllegalArgumentException("Trang thai khong hop le: " + trangThai);

        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Don hang khong ton tai: " + maDonHang));
        if ("Delivered".equals(donHang.getTrangThai()))
            throw new IllegalStateException("Khong the thay doi trang thai don hang da giao");

        donHang.setTrangThai(trangThai);
        return toResponse(donHangRepository.save(donHang));
    }

    // ── HUY DON HANG ─────────────────────────────────────────────────────────
    @Transactional
    public DonHangResponse huyDonHang(Integer maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Don hang khong ton tai: " + maDonHang));
        if ("Delivered".equals(donHang.getTrangThai()))
            throw new IllegalStateException("Khong the huy don hang da giao");
        if ("Cancelled".equals(donHang.getTrangThai()))
            throw new IllegalStateException("Don hang nay da bi huy truoc do");

        // Hoan tra ton kho
        Integer maKho = donHang.getKho().getMaKho();
        chiTietDonHangRepository.findById_MaDonHang(maDonHang).forEach(ct -> {
            TonKhoId tkId = new TonKhoId(maKho, ct.getSanPham().getMaSanPham());
            tonKhoRepository.findById(tkId).ifPresent(tk -> {
                tk.setSoLuong(tk.getSoLuong() + ct.getSoLuong());
                tonKhoRepository.save(tk);
            });
        });

        donHang.setTrangThai("Cancelled");
        thanhToanRepository.findByDonHang_MaDonHang(maDonHang).ifPresent(tt -> {
            tt.setTrangThai("Refunded");
            thanhToanRepository.save(tt);
        });
        return toResponse(donHangRepository.save(donHang));
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    public DonHangResponse toResponse(DonHang dh) {
        List<DonHangResponse.ChiTietResponse> chiTietResponses =
                (dh.getChiTietList() == null ? List.<ChiTietDonHang>of() : dh.getChiTietList())
                .stream().map(ct -> DonHangResponse.ChiTietResponse.builder()
                        .maSanPham(ct.getSanPham().getMaSanPham())
                        .tenSanPham(ct.getSanPham().getTenSanPham())
                        .soLuong(ct.getSoLuong()).donGia(ct.getDonGia())
                        .thanhTien(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                        .build())
                .collect(Collectors.toList());

        BigDecimal tongTien = chiTietResponses.stream()
                .map(DonHangResponse.ChiTietResponse::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ThanhToanResponse ttResp = null;
        if (dh.getThanhToan() != null) {
            ThanhToan tt = dh.getThanhToan();
            ttResp = ThanhToanResponse.builder()
                    .maThanhToan(tt.getMaThanhToan()).maDonHang(dh.getMaDonHang())
                    .phuongThuc(tt.getPhuongThuc()).trangThai(tt.getTrangThai())
                    .ngayThanhToan(tt.getNgayThanhToan()).build();
        }

        return DonHangResponse.builder()
                .maDonHang(dh.getMaDonHang()).ngayLap(dh.getNgayLap())
                .trangThai(dh.getTrangThai())
                .maKho(dh.getKho().getMaKho()).tenKho(dh.getKho().getTenKho())
                .maKhachHang(dh.getKhachHang().getMaKhachHang())
                .tenKhachHang(dh.getKhachHang().getTenKhachHang())
                .chiTiet(chiTietResponses).tongTien(tongTien).thanhToan(ttResp)
                .build();
    }
}
