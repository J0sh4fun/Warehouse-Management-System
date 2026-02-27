package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DonHangRequest {

    @NotNull(message = "MaKho khong duoc de trong")
    private Integer maKho;

    @NotNull(message = "MaKhachHang khong duoc de trong")
    private Integer maKhachHang;

    @NotNull(message = "Danh sach san pham khong duoc de trong")
    @Size(min = 1, message = "Don hang phai co it nhat 1 san pham")
    private List<ChiTietRequest> chiTiet;

    @Data
    public static class ChiTietRequest {

        @NotNull(message = "MaSanPham khong duoc de trong")
        private Integer maSanPham;

        @NotNull(message = "So luong khong duoc de trong")
        @Min(value = 1, message = "So luong phai lon hon 0")
        private Integer soLuong;

        @NotNull(message = "Don gia khong duoc de trong")
        @Min(value = 0, message = "Don gia khong duoc am")
        private BigDecimal donGia;
    }
}

