package com.example.warehousedb.repository;

import com.example.warehousedb.entity.TonKho;
import com.example.warehousedb.entity.TonKhoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, TonKhoId> {

    // Ton kho cua 1 kho cu the
    List<TonKho> findById_MaKho(Integer maKho);

    // Ton kho cua 1 san pham o tat ca cac kho
    List<TonKho> findById_MaSanPham(Integer maSanPham);

    // Tim ton kho theo ca kho lan san pham
    Optional<TonKho> findById_MaKhoAndId_MaSanPham(Integer maKho, Integer maSanPham);

    // Tong so luong ton kho cua 1 san pham tren tat ca cac kho
    @Query("SELECT COALESCE(SUM(t.soLuong), 0) FROM TonKho t WHERE t.id.maSanPham = :maSanPham")
    Integer sumSoLuongBySanPham(@Param("maSanPham") Integer maSanPham);
}

