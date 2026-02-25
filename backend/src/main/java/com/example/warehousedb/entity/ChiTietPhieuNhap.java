package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietPhieuNhap")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChiTietPhieuNhap {

    @EmbeddedId
    private ChiTietPhieuNhapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPhieuNhap")
    @JoinColumn(name = "MaPhieuNhap")
    private PhieuNhap phieuNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSanPham")
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "GiaNhap", precision = 12, scale = 2)
    private BigDecimal giaNhap;
}

