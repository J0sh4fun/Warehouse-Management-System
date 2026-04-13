package com.example.warehousedb.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DonHangResponse {

    private Integer maDonHang;
    private LocalDate ngayLap;
    private String trangThai;

    private Integer maKho;
    private String tenKho;

    private Integer maKhachHang;
    private String tenKhachHang;

    private List<ChiTietResponse> chiTiet;
    private BigDecimal tongTien;
    private ThanhToanResponse thanhToan;

    @Data
    @Builder
    public static class ChiTietResponse {
        private Integer maSanPham;
        private String tenSanPham;
        private Integer soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;
    }
}

