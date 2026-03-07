-- ============================================================
-- SALE & PAYMENT MODULE - FULL SETUP SCRIPT
-- Chay file nay trong MySQL Workbench hoac mysql CLI
-- ============================================================

USE WarehouseRentalDB;

-- ==============================================================
-- PHAN 1: TRIGGERS
-- ==============================================================

DROP TRIGGER IF EXISTS trg_giam_tonkho;
DROP TRIGGER IF EXISTS trg_hoantra_tonkho;
DROP TRIGGER IF EXISTS trg_tang_tonkho;

DELIMITER $$

-- 1. GIAM TON KHO KHI BAN HANG
CREATE TRIGGER trg_giam_tonkho
BEFORE INSERT ON ChiTietDonHang
FOR EACH ROW
BEGIN
    DECLARE v_ton   INT DEFAULT 0;
    DECLARE v_maKho INT;

    SELECT MaKho INTO v_maKho
    FROM DonHang WHERE MaDonHang = NEW.MaDonHang;

    SELECT COALESCE(SoLuong, 0) INTO v_ton
    FROM TonKho
    WHERE MaKho = v_maKho AND MaSanPham = NEW.MaSanPham;

    IF v_ton < NEW.SoLuong THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong du ton kho de thuc hien giao dich';
    ELSE
        UPDATE TonKho
        SET SoLuong = SoLuong - NEW.SoLuong
        WHERE MaKho = v_maKho AND MaSanPham = NEW.MaSanPham;
    END IF;
END$$

-- 2. HOAN TRA TON KHO KHI HUY DON HANG
CREATE TRIGGER trg_hoantra_tonkho
BEFORE DELETE ON ChiTietDonHang
FOR EACH ROW
BEGIN
    DECLARE v_maKho     INT;
    DECLARE v_trangThai VARCHAR(50);

    SELECT MaKho, TrangThai INTO v_maKho, v_trangThai
    FROM DonHang WHERE MaDonHang = OLD.MaDonHang;

    IF v_trangThai != 'Delivered' THEN
        UPDATE TonKho
        SET SoLuong = SoLuong + OLD.SoLuong
        WHERE MaKho = v_maKho AND MaSanPham = OLD.MaSanPham;
    END IF;
END$$

-- 3. TANG TON KHO KHI NHAP HANG
CREATE TRIGGER trg_tang_tonkho
AFTER INSERT ON ChiTietPhieuNhap
FOR EACH ROW
BEGIN
    DECLARE v_maKho INT;

    SELECT MaKho INTO v_maKho
    FROM PhieuNhap WHERE MaPhieuNhap = NEW.MaPhieuNhap;

    INSERT INTO TonKho (MaKho, MaSanPham, SoLuong)
    VALUES (v_maKho, NEW.MaSanPham, NEW.SoLuong)
    ON DUPLICATE KEY UPDATE SoLuong = SoLuong + NEW.SoLuong;
END$$

DELIMITER ;

-- ==============================================================
-- PHAN 2: VIEWS
-- ==============================================================

-- 1. DOANH THU THEO THANG
CREATE OR REPLACE VIEW View_DoanhThuThang AS
SELECT
    YEAR(dh.NgayLap)  AS Nam,
    MONTH(dh.NgayLap) AS Thang,
    COUNT(DISTINCT dh.MaDonHang)        AS SoDonHang,
    SUM(ct.SoLuong * ct.DonGia)         AS TongDoanhThu
FROM DonHang dh
JOIN ChiTietDonHang ct ON dh.MaDonHang = ct.MaDonHang
WHERE dh.TrangThai != 'Cancelled'
GROUP BY YEAR(dh.NgayLap), MONTH(dh.NgayLap)
ORDER BY Nam DESC, Thang DESC;

-- 2. TOP SAN PHAM BAN CHAY
CREATE OR REPLACE VIEW View_TopSanPham AS
SELECT
    sp.MaSanPham,
    sp.TenSanPham,
    dm.TenDanhMuc,
    SUM(ct.SoLuong)             AS TongSoLuongBan,
    SUM(ct.SoLuong * ct.DonGia) AS TongDoanhThu
FROM ChiTietDonHang ct
JOIN SanPham  sp ON ct.MaSanPham = sp.MaSanPham
JOIN DanhMuc  dm ON sp.MaDanhMuc = dm.MaDanhMuc
JOIN DonHang  dh ON ct.MaDonHang = dh.MaDonHang
WHERE dh.TrangThai != 'Cancelled'
GROUP BY sp.MaSanPham, sp.TenSanPham, dm.TenDanhMuc
ORDER BY TongSoLuongBan DESC;

-- 3. CHI TIET DON HANG (joined view)
CREATE OR REPLACE VIEW View_ChiTietDonHang AS
SELECT
    dh.MaDonHang,
    dh.NgayLap,
    dh.TrangThai        AS TrangThaiDonHang,
    kh.MaKhachHang,
    kh.TenKhachHang,
    kh.Email,
    k.MaKho,
    k.TenKho,
    sp.MaSanPham,
    sp.TenSanPham,
    ct.SoLuong,
    ct.DonGia,
    (ct.SoLuong * ct.DonGia) AS ThanhTien,
    tt.MaThanhToan,
    tt.PhuongThuc,
    tt.TrangThai        AS TrangThaiThanhToan,
    tt.NgayThanhToan
FROM DonHang dh
JOIN KhachHang      kh ON dh.MaKhachHang = kh.MaKhachHang
JOIN Kho             k  ON dh.MaKho       = k.MaKho
JOIN ChiTietDonHang ct  ON dh.MaDonHang   = ct.MaDonHang
JOIN SanPham        sp  ON ct.MaSanPham   = sp.MaSanPham
LEFT JOIN ThanhToan tt  ON dh.MaDonHang   = tt.MaDonHang;

-- 4. TONG HOA DON
CREATE OR REPLACE VIEW View_TongHoaDon AS
SELECT
    dh.MaDonHang,
    dh.NgayLap,
    dh.TrangThai,
    kh.TenKhachHang,
    k.TenKho,
    SUM(ct.SoLuong * ct.DonGia) AS TongTien,
    tt.PhuongThuc,
    tt.TrangThai   AS TrangThaiThanhToan,
    tt.NgayThanhToan
FROM DonHang dh
JOIN KhachHang      kh ON dh.MaKhachHang = kh.MaKhachHang
JOIN Kho             k  ON dh.MaKho       = k.MaKho
JOIN ChiTietDonHang ct  ON dh.MaDonHang   = ct.MaDonHang
LEFT JOIN ThanhToan tt  ON dh.MaDonHang   = tt.MaDonHang
GROUP BY
    dh.MaDonHang, dh.NgayLap, dh.TrangThai,
    kh.TenKhachHang, k.TenKho,
    tt.PhuongThuc, tt.TrangThai, tt.NgayThanhToan;

-- 5. DOANH THU THEO KHACH HANG
CREATE OR REPLACE VIEW View_DoanhThuKhachHang AS
SELECT
    kh.MaKhachHang,
    kh.TenKhachHang,
    kh.Email,
    COUNT(DISTINCT dh.MaDonHang)        AS TongDonHang,
    SUM(ct.SoLuong * ct.DonGia)         AS TongChiTieu
FROM KhachHang kh
JOIN DonHang        dh ON kh.MaKhachHang = dh.MaKhachHang
JOIN ChiTietDonHang ct ON dh.MaDonHang   = ct.MaDonHang
WHERE dh.TrangThai != 'Cancelled'
GROUP BY kh.MaKhachHang, kh.TenKhachHang, kh.Email
ORDER BY TongChiTieu DESC;

-- ==============================================================
-- PHAN 3: STORED PROCEDURES
-- ==============================================================

DROP PROCEDURE IF EXISTS TaoDonHang;
DROP PROCEDURE IF EXISTS CapNhatTrangThaiDonHang;
DROP PROCEDURE IF EXISTS ThanhToanDonHang;
DROP PROCEDURE IF EXISTS BaoCaoDoanhThu;
DROP PROCEDURE IF EXISTS HuyDonHang;

DELIMITER $$

-- 1. TAO DON HANG
CREATE PROCEDURE TaoDonHang(
    IN p_MaKho       INT,
    IN p_MaKhachHang INT,
    IN p_NgayLap     DATE
)
BEGIN
    INSERT INTO DonHang (MaKho, MaKhachHang, NgayLap, TrangThai)
    VALUES (p_MaKho, p_MaKhachHang, p_NgayLap, 'Pending');
    SELECT LAST_INSERT_ID() AS MaDonHang;
END$$

-- 2. CAP NHAT TRANG THAI DON HANG
CREATE PROCEDURE CapNhatTrangThaiDonHang(
    IN p_MaDonHang INT,
    IN p_TrangThai VARCHAR(50)
)
BEGIN
    IF p_TrangThai NOT IN ('Pending','Processing','Delivered','Cancelled') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trang thai khong hop le';
    END IF;

    IF EXISTS (
        SELECT 1 FROM DonHang
        WHERE MaDonHang = p_MaDonHang AND TrangThai = 'Delivered'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong the thay doi trang thai don hang da giao';
    END IF;

    UPDATE DonHang SET TrangThai = p_TrangThai WHERE MaDonHang = p_MaDonHang;
END$$

-- 3. THANH TOAN DON HANG
CREATE PROCEDURE ThanhToanDonHang(
    IN p_MaDonHang    INT,
    IN p_PhuongThuc   VARCHAR(50),
    IN p_NgayThanhToan DATE
)
BEGIN
    DECLARE v_TrangThaiDon VARCHAR(50);

    SELECT TrangThai INTO v_TrangThaiDon
    FROM DonHang WHERE MaDonHang = p_MaDonHang;

    IF v_TrangThaiDon IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Don hang khong ton tai';
    END IF;
    IF v_TrangThaiDon = 'Cancelled' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Khong the thanh toan don hang da huy';
    END IF;
    IF EXISTS (
        SELECT 1 FROM ThanhToan
        WHERE MaDonHang = p_MaDonHang AND TrangThai = 'Paid'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Don hang nay da duoc thanh toan';
    END IF;

    INSERT INTO ThanhToan (MaDonHang, PhuongThuc, TrangThai, NgayThanhToan)
    VALUES (p_MaDonHang, p_PhuongThuc, 'Paid', p_NgayThanhToan)
    ON DUPLICATE KEY UPDATE
        PhuongThuc    = p_PhuongThuc,
        TrangThai     = 'Paid',
        NgayThanhToan = p_NgayThanhToan;

    UPDATE DonHang SET TrangThai = 'Delivered' WHERE MaDonHang = p_MaDonHang;
END$$

-- 4. BAO CAO DOANH THU THEO KHOANG THOI GIAN
CREATE PROCEDURE BaoCaoDoanhThu(
    IN p_TuNgay  DATE,
    IN p_DenNgay DATE
)
BEGIN
    SELECT
        dh.MaDonHang,
        dh.NgayLap,
        kh.TenKhachHang,
        k.TenKho,
        SUM(ct.SoLuong * ct.DonGia) AS TongTien,
        tt.PhuongThuc,
        tt.TrangThai AS TrangThaiThanhToan
    FROM DonHang dh
    JOIN KhachHang      kh ON dh.MaKhachHang = kh.MaKhachHang
    JOIN Kho             k  ON dh.MaKho       = k.MaKho
    JOIN ChiTietDonHang ct  ON dh.MaDonHang   = ct.MaDonHang
    LEFT JOIN ThanhToan tt  ON dh.MaDonHang   = tt.MaDonHang
    WHERE dh.NgayLap BETWEEN p_TuNgay AND p_DenNgay
      AND dh.TrangThai != 'Cancelled'
    GROUP BY
        dh.MaDonHang, dh.NgayLap, kh.TenKhachHang,
        k.TenKho, tt.PhuongThuc, tt.TrangThai
    ORDER BY dh.NgayLap DESC;
END$$

-- 5. HUY DON HANG (hoan tra ton kho)
CREATE PROCEDURE HuyDonHang(
    IN p_MaDonHang INT
)
BEGIN
    DECLARE v_TrangThai VARCHAR(50);
    DECLARE v_MaKho     INT;

    SELECT TrangThai, MaKho INTO v_TrangThai, v_MaKho
    FROM DonHang WHERE MaDonHang = p_MaDonHang;

    IF v_TrangThai = 'Delivered' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Khong the huy don hang da giao';
    END IF;
    IF v_TrangThai = 'Cancelled' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Don hang nay da bi huy truoc do';
    END IF;

    -- Hoan tra ton kho
    UPDATE TonKho tk
    JOIN ChiTietDonHang ct
        ON tk.MaKho = v_MaKho AND tk.MaSanPham = ct.MaSanPham
    SET tk.SoLuong = tk.SoLuong + ct.SoLuong
    WHERE ct.MaDonHang = p_MaDonHang;

    UPDATE DonHang   SET TrangThai = 'Cancelled' WHERE MaDonHang = p_MaDonHang;
    UPDATE ThanhToan SET TrangThai = 'Refunded'  WHERE MaDonHang = p_MaDonHang;
END$$

DELIMITER ;

-- ==============================================================
-- VERIFY
-- ==============================================================
SELECT 'TRIGGERS:' AS '';
SELECT Trigger_Name, Event_Manipulation, Event_Object_Table
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = 'WarehouseRentalDB';

SELECT 'VIEWS:' AS '';
SELECT TABLE_NAME AS ViewName
FROM information_schema.VIEWS
WHERE TABLE_SCHEMA = 'WarehouseRentalDB';

SELECT 'STORED PROCEDURES:' AS '';
SELECT ROUTINE_NAME AS ProcedureName
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = 'WarehouseRentalDB' AND ROUTINE_TYPE = 'PROCEDURE';

