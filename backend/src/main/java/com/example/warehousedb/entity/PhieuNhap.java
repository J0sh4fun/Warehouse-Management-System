package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PhieuNhap")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhieuNhap")
    private Integer maPhieuNhap;

    @Column(name = "NgayNhap")
    private LocalDate ngayNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKho")
    private Kho kho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNCC")
    private NhaCungCap nhaCungCap;

    @OneToMany(mappedBy = "phieuNhap", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietPhieuNhap> chiTietList;
}

