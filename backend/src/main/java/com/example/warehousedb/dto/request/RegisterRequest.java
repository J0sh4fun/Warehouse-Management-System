package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Ten khong duoc de trong")
    private String ten;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "Mat khau khong duoc de trong")
    @Size(min = 6, message = "Mat khau phai co it nhat 6 ky tu")
    private String matKhau;

    /** Chi dung khi dang ky KhachHang */
    private String dienThoai;

    /** Chi dung khi dang ky KhachHang */
    private String diaChi;
}

