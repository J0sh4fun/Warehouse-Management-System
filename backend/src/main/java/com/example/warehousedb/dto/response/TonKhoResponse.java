package com.example.warehousedb.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TonKhoResponse {

    private Integer maKho;
    private String tenKho;
    private Integer maSanPham;
    private String tenSanPham;
    private BigDecimal giaBan;
    private Integer soLuong;
    private String trangThaiTon;  // DU, THAP, SAP HET, HET
}

