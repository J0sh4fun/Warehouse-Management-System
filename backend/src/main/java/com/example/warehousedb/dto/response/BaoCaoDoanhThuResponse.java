package com.example.warehousedb.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BaoCaoDoanhThuResponse {
    private Integer nam;
    private Integer thang;
    private Long soDonHang;
    private BigDecimal tongDoanhThu;
}

