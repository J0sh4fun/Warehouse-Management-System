package com.example.warehousedb.repository;

import com.example.warehousedb.entity.PhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Integer> {

    List<PhieuNhap> findByKho_MaKho(Integer maKho);

    List<PhieuNhap> findByNhaCungCap_MaNCC(Integer maNCC);

    List<PhieuNhap> findByNgayNhapBetween(LocalDate from, LocalDate to);
}

