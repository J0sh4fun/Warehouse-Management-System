package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DanhMucRequest {

    @NotBlank(message = "Ten danh muc khong duoc de trong")
    @Size(min = 1, max = 100, message = "Ten danh muc phai tu 1 den 100 ky tu")
    private String tenDanhMuc;
}

