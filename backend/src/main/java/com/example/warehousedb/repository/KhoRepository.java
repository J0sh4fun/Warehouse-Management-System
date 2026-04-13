package com.example.warehousedb.repository;

import com.example.warehousedb.entity.Admin;
import com.example.warehousedb.entity.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhoRepository extends JpaRepository<Kho, Integer> {

    List<Kho> findByTrangThai(String trangThai);

    List<Kho> findByAdmin(Admin admin);

    List<Kho> findByAdmin_MaAdmin(Integer maAdmin);

    boolean existsByTenKho(String tenKho);
}

