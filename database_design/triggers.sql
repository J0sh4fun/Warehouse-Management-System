-- TU DONG QUAN LY TON KHO KHI BAN HANG VA NHAP HANG

DELIMITER $$

-- 1. GIAM TON KHO KHI BAN (BEFORE INSERT on ChiTietDonHang)
DROP TRIGGER IF EXISTS trg_giam_tonkho$$
CREATE TRIGGER trg_giam_tonkho
BEFORE INSERT ON ChiTietDonHang
FOR EACH ROW
BEGIN
    DECLARE v_ton     INT DEFAULT 0;
    DECLARE v_maKho   INT;

    -- Lay MaKho tu don hang
    SELECT MaKho INTO v_maKho
    FROM DonHang
    WHERE MaDonHang = NEW.MaDonHang;

    -- Lay so luong ton kho hien tai
    SELECT COALESCE(SoLuong, 0) INTO v_ton
    FROM TonKho
    WHERE MaKho = v_maKho AND MaSanPham = NEW.MaSanPham;

    -- Kiem tra du ton kho
    IF v_ton < NEW.SoLuong THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong du ton kho de thuc hien giao dich';
    ELSE
        UPDATE TonKho
        SET SoLuong = SoLuong - NEW.SoLuong
        WHERE MaKho = v_maKho AND MaSanPham = NEW.MaSanPham;
    END IF;
END$$

-- 2. HOAN TRA TON KHO KHI XOA CHI TIET DON HANG (BEFORE DELETE)
DROP TRIGGER IF EXISTS trg_hoantra_tonkho$$
CREATE TRIGGER trg_hoantra_tonkho
BEFORE DELETE ON ChiTietDonHang
FOR EACH ROW
BEGIN
    DECLARE v_maKho   INT;
    DECLARE v_trangThai VARCHAR(50);

    SELECT MaKho, TrangThai INTO v_maKho, v_trangThai
    FROM DonHang WHERE MaDonHang = OLD.MaDonHang;

    -- Chi hoan tra khi don hang chua giao
    IF v_trangThai != 'Delivered' THEN
        UPDATE TonKho
        SET SoLuong = SoLuong + OLD.SoLuong
        WHERE MaKho = v_maKho AND MaSanPham = OLD.MaSanPham;
    END IF;
END$$

-- 3. TANG TON KHO KHI NHAP HANG (AFTER INSERT on ChiTietPhieuNhap)
DROP TRIGGER IF EXISTS trg_tang_tonkho$$
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
