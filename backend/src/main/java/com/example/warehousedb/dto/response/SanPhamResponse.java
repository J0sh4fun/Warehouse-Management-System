package com.example.warehousedb.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SanPhamResponse {

    private Integer maSanPham;
    private String tenSanPham;
    private BigDecimal giaBan;
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private Integer maKhachHang;
    private String tenKhachHang;
}

