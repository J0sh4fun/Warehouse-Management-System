package com.example.warehousedb.config;

import com.example.warehousedb.entity.Admin;
import com.example.warehousedb.entity.DanhMuc;
import com.example.warehousedb.entity.KhachHang;
import com.example.warehousedb.entity.Kho;
import com.example.warehousedb.entity.NhaCungCap;
import com.example.warehousedb.entity.SanPham;
import com.example.warehousedb.entity.TonKho;
import com.example.warehousedb.entity.TonKhoId;
import com.example.warehousedb.repository.AdminRepository;
import com.example.warehousedb.repository.DanhMucRepository;
import com.example.warehousedb.repository.KhachHangRepository;
import com.example.warehousedb.repository.KhoRepository;
import com.example.warehousedb.repository.NhaCungCapRepository;
import com.example.warehousedb.repository.SanPhamRepository;
import com.example.warehousedb.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final AdminRepository adminRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final KhoRepository khoRepository;
    private final SanPhamRepository sanPhamRepository;
    private final TonKhoRepository tonKhoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Initializing database with test data...");
            ensureInventoryTriggers();

            if (adminRepository.count() == 0) {
                Admin admin = Admin.builder()
                        .tenAdmin("Administrator")
                        .email("root@example.com")
                        .matKhau(passwordEncoder.encode("hung1342005"))
                        .build();
                adminRepository.save(admin);
                log.info("Admin created: root@example.com / hung1342005");
            }

            if (khachHangRepository.count() == 0) {
                KhachHang kh1 = KhachHang.builder()
                        .tenKhachHang("Khach Hang 1")
                        .email("customer1@example.com")
                        .matKhau(passwordEncoder.encode("password123"))
                        .dienThoai("0123456789")
                        .diaChi("123 Main Street, City")
                        .build();
                khachHangRepository.save(kh1);

                KhachHang kh2 = KhachHang.builder()
                        .tenKhachHang("Khach Hang 2")
                        .email("customer2@example.com")
                        .matKhau(passwordEncoder.encode("password123"))
                        .dienThoai("0987654321")
                        .diaChi("456 Oak Avenue, City")
                        .build();
                khachHangRepository.save(kh2);
                log.info("Created 2 test customers");
            }

            if (danhMucRepository.count() == 0) {
                DanhMuc dm1 = DanhMuc.builder().tenDanhMuc("Electronics").build();
                DanhMuc dm2 = DanhMuc.builder().tenDanhMuc("Furniture").build();
                DanhMuc dm3 = DanhMuc.builder().tenDanhMuc("Textiles").build();
                danhMucRepository.save(dm1);
                danhMucRepository.save(dm2);
                danhMucRepository.save(dm3);
                log.info("Created 3 test categories");
            }

            if (nhaCungCapRepository.count() == 0) {
                NhaCungCap ncc1 = NhaCungCap.builder()
                        .tenNCC("Supplier ABC")
                        .dienThoai("0111222333")
                        .diaChi("789 Industrial Blvd")
                        .build();
                NhaCungCap ncc2 = NhaCungCap.builder()
                        .tenNCC("Supplier XYZ")
                        .dienThoai("0444555666")
                        .diaChi("321 Commerce Drive")
                        .build();
                nhaCungCapRepository.save(ncc1);
                nhaCungCapRepository.save(ncc2);
                log.info("Created 2 test suppliers");
            }

            Admin admin = adminRepository.findAll().stream().findFirst().orElse(null);
            if (admin != null && khoRepository.count() == 0) {
                Kho kho1 = Kho.builder()
                        .tenKho("Kho Trung Tam")
                        .diaChi("100 Storage Lane, Industrial Park")
                        .dienTich(5000f)
                        .trangThai("Available")
                        .admin(admin)
                        .build();
                Kho kho2 = Kho.builder()
                        .tenKho("Kho Phia Bac")
                        .diaChi("200 North Street")
                        .dienTich(3000f)
                        .trangThai("Available")
                        .admin(admin)
                        .build();
                khoRepository.save(kho1);
                khoRepository.save(kho2);
                log.info("Created 2 test warehouses");
            }

            DanhMuc dm = danhMucRepository.findAll().stream().findFirst().orElse(null);
            KhachHang kh = khachHangRepository.findAll().stream().findFirst().orElse(null);
            if (dm != null && kh != null && sanPhamRepository.count() == 0) {
                SanPham sp1 = SanPham.builder()
                        .tenSanPham("Laptop Dell XPS 13")
                        .giaBan(BigDecimal.valueOf(1200.00))
                        .danhMuc(dm)
                        .khachHang(kh)
                        .build();
                SanPham sp2 = SanPham.builder()
                        .tenSanPham("Office Chair Pro")
                        .giaBan(BigDecimal.valueOf(250.00))
                        .danhMuc(dm)
                        .khachHang(kh)
                        .build();
                SanPham sp3 = SanPham.builder()
                        .tenSanPham("Cotton Fabric Roll")
                        .giaBan(BigDecimal.valueOf(50.00))
                        .danhMuc(dm)
                        .khachHang(kh)
                        .build();
                sanPhamRepository.save(sp1);
                sanPhamRepository.save(sp2);
                sanPhamRepository.save(sp3);
                log.info("Created 3 test products");
            }

            Kho kho = khoRepository.findAll().stream().findFirst().orElse(null);
            if (kho != null && tonKhoRepository.count() == 0) {
                sanPhamRepository.findAll().forEach(sp -> {
                    TonKho tonKho = TonKho.builder()
                            .id(new TonKhoId(kho.getMaKho(), sp.getMaSanPham()))
                            .kho(kho)
                            .sanPham(sp)
                            .soLuong(100)
                            .build();
                    tonKhoRepository.save(tonKho);
                });
                log.info("Created initial inventory for products");
            }

            log.info("Database initialization completed");
        };
    }

    private void ensureInventoryTriggers() {
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_giam_tonkho");
        jdbcTemplate.execute("""
                CREATE TRIGGER trg_giam_tonkho
                BEFORE INSERT ON ChiTietDonHang
                FOR EACH ROW
                BEGIN
                    DECLARE ton INT;
                    SELECT SoLuong INTO ton
                    FROM TonKho
                    WHERE MaKho = (SELECT MaKho FROM DonHang WHERE MaDonHang = NEW.MaDonHang)
                      AND MaSanPham = NEW.MaSanPham;
                    IF ton IS NULL OR ton < NEW.SoLuong THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Khong du ton kho';
                    ELSE
                        UPDATE TonKho
                        SET SoLuong = SoLuong - NEW.SoLuong
                        WHERE MaKho = (SELECT MaKho FROM DonHang WHERE MaDonHang = NEW.MaDonHang)
                          AND MaSanPham = NEW.MaSanPham;
                    END IF;
                END
                """);

        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_tang_tonkho");
        jdbcTemplate.execute("""
                CREATE TRIGGER trg_tang_tonkho
                AFTER INSERT ON ChiTietPhieuNhap
                FOR EACH ROW
                BEGIN
                    INSERT INTO TonKho (MaKho, MaSanPham, SoLuong)
                    VALUES (
                        (SELECT MaKho FROM PhieuNhap WHERE MaPhieuNhap = NEW.MaPhieuNhap),
                        NEW.MaSanPham,
                        NEW.SoLuong
                    )
                    ON DUPLICATE KEY UPDATE SoLuong = SoLuong + NEW.SoLuong;
                END
                """);

        log.info("Inventory triggers ensured");
    }
}
