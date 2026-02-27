package com.example.warehousedb.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ThanhToanResponse {
    private Integer maThanhToan;
    private Integer maDonHang;
    private String phuongThuc;
    private String trangThai;
    private LocalDate ngayThanhToan;
}

