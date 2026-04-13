package com.example.warehousedb.repository;

import com.example.warehousedb.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    List<DonHang> findByKhachHang_MaKhachHang(Integer maKhachHang);

    List<DonHang> findByKho_MaKho(Integer maKho);

    List<DonHang> findByTrangThai(String trangThai);

    List<DonHang> findByKhachHang_MaKhachHangAndTrangThai(Integer maKhachHang, String trangThai);
}

