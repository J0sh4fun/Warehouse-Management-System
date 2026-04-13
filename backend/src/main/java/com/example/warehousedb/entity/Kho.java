package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Kho")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Kho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKho")
    private Integer maKho;

    @Column(name = "TenKho", length = 100)
    private String tenKho;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "DienTich")
    private Float dienTich;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaAdmin")
    private Admin admin;

    @OneToMany(mappedBy = "kho", fetch = FetchType.LAZY)
    private List<HopDongThueKho> hopDongList;

    @OneToMany(mappedBy = "kho", fetch = FetchType.LAZY)
    private List<TonKho> tonKhoList;

    @OneToMany(mappedBy = "kho", fetch = FetchType.LAZY)
    private List<PhieuNhap> phieuNhapList;

    @OneToMany(mappedBy = "kho", fetch = FetchType.LAZY)
    private List<DonHang> donHangList;
}

