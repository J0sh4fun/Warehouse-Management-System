package com.example.warehousedb.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NhaCungCapResponse {

    private Integer maNCC;
    private String tenNCC;
    private String dienThoai;
    private String diaChi;
    private Integer soPhieuNhap;
}

