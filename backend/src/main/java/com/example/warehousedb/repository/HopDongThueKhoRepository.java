package com.example.warehousedb.repository;

import com.example.warehousedb.entity.HopDongThueKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HopDongThueKhoRepository extends JpaRepository<HopDongThueKho, Integer> {

    List<HopDongThueKho> findByKhachHang_MaKhachHang(Integer maKhachHang);

    List<HopDongThueKho> findByKho_MaKho(Integer maKho);

    // Kiem tra kho co hop dong con hieu luc khong
    boolean existsByKho_MaKhoAndNgayKetThucAfter(Integer maKho, LocalDate date);
}

