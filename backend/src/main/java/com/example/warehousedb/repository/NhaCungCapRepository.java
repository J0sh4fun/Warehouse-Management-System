package com.example.warehousedb.repository;

import com.example.warehousedb.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Integer> {

    Optional<NhaCungCap> findByTenNCC(String tenNCC);

    boolean existsByTenNCC(String tenNCC);
}

