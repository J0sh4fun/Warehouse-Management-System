package com.example.warehousedb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class ChiTietDonHangId implements Serializable {

    @Column(name = "MaDonHang")
    private Integer maDonHang;

    @Column(name = "MaSanPham")
    private Integer maSanPham;
}

