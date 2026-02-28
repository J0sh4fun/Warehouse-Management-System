package com.example.warehousedb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuNhapResponse {

    private Integer maPhieuNhap;
    private LocalDate ngayNhap;
    private Integer maKho;
    private String tenKho;
    private Integer maNCC;
    private String tenNCC;
    private Integer soChiTiet;
    private BigDecimal tongTien;
}

