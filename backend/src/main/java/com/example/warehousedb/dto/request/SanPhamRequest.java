package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SanPhamRequest {

    @NotBlank(message = "Ten san pham khong duoc de trong")
    @Size(min = 1, max = 100, message = "Ten san pham phai tu 1 den 100 ky tu")
    private String tenSanPham;

    @NotNull(message = "Gia ban khong duoc de trong")
    @DecimalMin(value = "0.01", message = "Gia ban phai lon hon 0")
    private BigDecimal giaBan;

    @NotNull(message = "Ma danh muc khong duoc de trong")
    private Integer maDanhMuc;

    @NotNull(message = "Ma khach hang khong duoc de trong")
    private Integer maKhachHang;
}

