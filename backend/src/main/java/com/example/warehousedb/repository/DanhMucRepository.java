package com.example.warehousedb.repository;

import com.example.warehousedb.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    Optional<DanhMuc> findByTenDanhMuc(String tenDanhMuc);

    boolean existsByTenDanhMuc(String tenDanhMuc);
}

