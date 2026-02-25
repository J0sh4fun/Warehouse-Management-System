package com.example.warehousedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Admin")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaAdmin")
    private Integer maAdmin;

    @Column(name = "TenAdmin", length = 100)
    private String tenAdmin;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "MatKhau", length = 255)
    private String matKhau;

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<Kho> danhSachKho;
}

