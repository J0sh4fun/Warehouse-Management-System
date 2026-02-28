package com.example.warehousedb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucResponse {

    private Integer maDanhMuc;
    private String tenDanhMuc;
    private Integer soSanPham;
}

