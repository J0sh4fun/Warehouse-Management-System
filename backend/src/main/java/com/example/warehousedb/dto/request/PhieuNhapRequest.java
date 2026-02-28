package com.example.warehousedb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PhieuNhapRequest {

    @NotNull(message = "Ma kho khong duoc de trong")
    private Integer maKho;

    @NotNull(message = "Ma nha cung cap khong duoc de trong")
    private Integer maNCC;

    @NotEmpty(message = "Chi tiet khong duoc de trong")
    private List<@Valid ChiTietRequest> chiTiet;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ChiTietRequest {
        @NotNull(message = "Ma san pham khong duoc de trong")
        private Integer maSanPham;

        @NotNull(message = "So luong khong duoc de trong")
        @Min(value = 1, message = "So luong phai >= 1")
        private Integer soLuong;

        @NotNull(message = "Gia nhap khong duoc de trong")
        @DecimalMin(value = "0.01", message = "Gia nhap phai > 0")
        private BigDecimal giaNhap;
    }
}

