package com.example.warehousedb.repository;

import com.example.warehousedb.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {

    Optional<ThanhToan> findByDonHang_MaDonHang(Integer maDonHang);

    List<ThanhToan> findByTrangThai(String trangThai);

    List<ThanhToan> findByPhuongThuc(String phuongThuc);

    boolean existsByDonHang_MaDonHang(Integer maDonHang);
}

