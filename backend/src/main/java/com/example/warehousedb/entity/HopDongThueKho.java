package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "HopDongThueKho")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HopDongThueKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHopDong")
    private Integer maHopDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKho")
    private Kho kho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang")
    private KhachHang khachHang;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "GiaThue", precision = 12, scale = 2)
    private BigDecimal giaThue;
}

