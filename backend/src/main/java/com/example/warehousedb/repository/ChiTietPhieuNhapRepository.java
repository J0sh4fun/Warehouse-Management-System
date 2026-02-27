package com.example.warehousedb.repository;

import com.example.warehousedb.entity.ChiTietPhieuNhap;
import com.example.warehousedb.entity.ChiTietPhieuNhapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, ChiTietPhieuNhapId> {

    // Lay tat ca chi tiet cua 1 phieu nhap
    List<ChiTietPhieuNhap> findById_MaPhieuNhap(Integer maPhieuNhap);

    // Lay lich su nhap cua 1 san pham
    List<ChiTietPhieuNhap> findById_MaSanPham(Integer maSanPham);
}

