package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.ThanhToanRequest;
import com.example.warehousedb.dto.response.ThanhToanResponse;
import com.example.warehousedb.entity.DonHang;
import com.example.warehousedb.entity.ThanhToan;
import com.example.warehousedb.repository.DonHangRepository;
import com.example.warehousedb.repository.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository   donHangRepository;

    private static final List<String> PHUONG_THUC_HOP_LE =
            List.of("Cash", "BankTransfer", "Momo", "VNPay");

    // ── THANH TOAN ────────────────────────────────────────────────────────────
    @Transactional
    public ThanhToanResponse thanhToan(ThanhToanRequest request) {
        if (!PHUONG_THUC_HOP_LE.contains(request.getPhuongThuc()))
            throw new IllegalArgumentException("Phuong thuc khong hop le. Chi chap nhan: " + PHUONG_THUC_HOP_LE);

        DonHang donHang = donHangRepository.findById(request.getMaDonHang())
                .orElseThrow(() -> new IllegalArgumentException("Don hang khong ton tai: " + request.getMaDonHang()));

        if ("Cancelled".equals(donHang.getTrangThai()))
            throw new IllegalStateException("Khong the thanh toan don hang da huy");

        thanhToanRepository.findByDonHang_MaDonHang(request.getMaDonHang()).ifPresent(tt -> {
            if ("Paid".equals(tt.getTrangThai()))
                throw new IllegalStateException("Don hang nay da duoc thanh toan");
        });

        LocalDate ngayTT = request.getNgayThanhToan() != null ? request.getNgayThanhToan() : LocalDate.now();

        ThanhToan thanhToan = thanhToanRepository
                .findByDonHang_MaDonHang(request.getMaDonHang())
                .orElse(ThanhToan.builder().donHang(donHang).build());

        thanhToan.setPhuongThuc(request.getPhuongThuc());
        thanhToan.setTrangThai("Paid");
        thanhToan.setNgayThanhToan(ngayTT);
        thanhToanRepository.save(thanhToan);

        donHang.setTrangThai("Delivered");
        donHangRepository.save(donHang);

        return toResponse(thanhToan);
    }

    // ── GET BY DON HANG ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ThanhToanResponse getByDonHang(Integer maDonHang) {
        return thanhToanRepository.findByDonHang_MaDonHang(maDonHang)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Don hang " + maDonHang + " chua co thanh toan"));
    }

    // ── GET BY TRANG THAI ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ThanhToanResponse> getByTrangThai(String trangThai) {
        return thanhToanRepository.findByTrangThai(trangThai).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    private ThanhToanResponse toResponse(ThanhToan tt) {
        return ThanhToanResponse.builder()
                .maThanhToan(tt.getMaThanhToan())
                .maDonHang(tt.getDonHang().getMaDonHang())
                .phuongThuc(tt.getPhuongThuc())
                .trangThai(tt.getTrangThai())
                .ngayThanhToan(tt.getNgayThanhToan())
                .build();
    }
}
