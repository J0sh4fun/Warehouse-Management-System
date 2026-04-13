package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ThanhToan")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaThanhToan")
    private Integer maThanhToan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", unique = true)
    private DonHang donHang;

    @Column(name = "PhuongThuc", length = 50)
    private String phuongThuc;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    @Column(name = "NgayThanhToan")
    private LocalDate ngayThanhToan;
}

