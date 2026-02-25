package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDonHang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChiTietDonHang {

    @EmbeddedId
    private ChiTietDonHangId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maDonHang")
    @JoinColumn(name = "MaDonHang")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSanPham")
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia", precision = 12, scale = 2)
    private BigDecimal donGia;
}

