package com.example.warehousedb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class TonKhoId implements Serializable {

    @Column(name = "MaKho")
    private Integer maKho;

    @Column(name = "MaSanPham")
    private Integer maSanPham;
}

