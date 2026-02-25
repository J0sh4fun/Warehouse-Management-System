package com.example.warehousedb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhieuNhapId implements Serializable {

    @Column(name = "MaPhieuNhap")
    private Integer maPhieuNhap;

    @Column(name = "MaSanPham")
    private Integer maSanPham;
}

