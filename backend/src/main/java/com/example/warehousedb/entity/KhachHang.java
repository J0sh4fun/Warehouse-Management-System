package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "KhachHang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKhachHang")
    private Integer maKhachHang;

    @Column(name = "TenKhachHang", length = 100)
    private String tenKhachHang;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "DienThoai", length = 20)
    private String dienThoai;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "MatKhau", length = 255)
    private String matKhau;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<HopDongThueKho> hopDongList;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<SanPham> sanPhamList;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<DonHang> donHangList;
}

