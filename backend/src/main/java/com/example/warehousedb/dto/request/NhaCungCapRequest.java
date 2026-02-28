package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NhaCungCapRequest {

    @NotBlank(message = "Ten nha cung cap khong duoc de trong")
    @Size(min = 1, max = 100, message = "Ten NCC phai tu 1 den 100 ky tu")
    private String tenNCC;

    @NotBlank(message = "Dien thoai khong duoc de trong")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Dien thoai phai la so tu 10-20 chu so")
    private String dienThoai;

    @NotBlank(message = "Dia chi khong duoc de trong")
    @Size(min = 5, max = 255, message = "Dia chi phai tu 5 den 255 ky tu")
    private String diaChi;
}

