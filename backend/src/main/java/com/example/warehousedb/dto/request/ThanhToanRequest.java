package com.example.warehousedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ThanhToanRequest {

    @NotNull(message = "MaDonHang khong duoc de trong")
    private Integer maDonHang;

    @NotBlank(message = "Phuong thuc thanh toan khong duoc de trong")
    private String phuongThuc;  // Cash | BankTransfer | Momo | VNPay

    private LocalDate ngayThanhToan;  // null → dung ngay hom nay
}

