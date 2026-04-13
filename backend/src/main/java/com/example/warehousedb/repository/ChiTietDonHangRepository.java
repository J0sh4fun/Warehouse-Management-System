package com.example.warehousedb.repository;

import com.example.warehousedb.entity.ChiTietDonHang;
import com.example.warehousedb.entity.ChiTietDonHangId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, ChiTietDonHangId> {

    // Lay tat ca chi tiet cua 1 don hang
    List<ChiTietDonHang> findById_MaDonHang(Integer maDonHang);

    // Lay tat ca don hang chua 1 san pham cu the
    List<ChiTietDonHang> findById_MaSanPham(Integer maSanPham);
}

