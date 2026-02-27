package com.example.warehousedb.repository;

import com.example.warehousedb.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByDanhMuc_MaDanhMuc(Integer maDanhMuc);

    List<SanPham> findByKhachHang_MaKhachHang(Integer maKhachHang);

    List<SanPham> findByTenSanPhamContainingIgnoreCase(String keyword);
}

