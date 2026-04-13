package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "NhaCungCap")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNCC")
    private Integer maNCC;

    @Column(name = "TenNCC", length = 100)
    private String tenNCC;

    @Column(name = "DienThoai", length = 20)
    private String dienThoai;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @OneToMany(mappedBy = "nhaCungCap", fetch = FetchType.LAZY)
    private List<PhieuNhap> phieuNhapList;
}

