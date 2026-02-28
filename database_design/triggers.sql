-- TỰ ĐỘNG QUẢN LÝ TỒN KHO KHI BÁN HÀNG VÀ NHẬP HÀNG

DELIMITER $$

-- GIAM TON KHO KHI BAN
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
END$$

-- TANG TON KHO KHI NHAP
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
END$$

DELIMITER ;
