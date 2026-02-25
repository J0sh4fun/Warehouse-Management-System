package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TonKho")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TonKho {

    @EmbeddedId
    private TonKhoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maKho")
    @JoinColumn(name = "MaKho")
    private Kho kho;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSanPham")
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @Column(name = "SoLuong")
    private Integer soLuong;
}

