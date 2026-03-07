-- ============================================================
-- STORED PROCEDURES - Sale & Payment Module
-- ============================================================

DELIMITER $$

-- 1. TAO DON HANG KEM CHI TIET (atomic transaction)
-- Su dung: CALL TaoDonHangDayDu(1, 2, '2026-02-27', '[{"maSanPham":1,"soLuong":2,"donGia":150000}]')
-- Note: Trong thuc te goi tu Java @Transactional thay vi JSON string
CREATE PROCEDURE TaoDonHang (
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
CREATE PROCEDURE CapNhatTrangThaiDonHang (
    IN p_MaDonHang INT,
    IN p_TrangThai VARCHAR(50)
)
BEGIN
    -- Validate trang thai hop le
    IF p_TrangThai NOT IN ('Pending', 'Processing', 'Delivered', 'Cancelled') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Trang thai khong hop le. Chi chap nhan: Pending, Processing, Delivered, Cancelled';
    END IF;

    -- Khong cho phep huy don hang da giao
    IF EXISTS (
        SELECT 1 FROM DonHang
        WHERE MaDonHang = p_MaDonHang AND TrangThai = 'Delivered'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong the huy don hang da giao';
    END IF;

    UPDATE DonHang
    SET TrangThai = p_TrangThai
    WHERE MaDonHang = p_MaDonHang;
END$$

-- 3. THANH TOAN DON HANG
CREATE PROCEDURE ThanhToanDonHang (
    IN p_MaDonHang   INT,
    IN p_PhuongThuc  VARCHAR(50),
    IN p_NgayThanhToan DATE
)
BEGIN
    DECLARE v_TrangThaiDon VARCHAR(50);

    -- Kiem tra don hang ton tai va trang thai
    SELECT TrangThai INTO v_TrangThaiDon
    FROM DonHang WHERE MaDonHang = p_MaDonHang;

    IF v_TrangThaiDon IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Don hang khong ton tai';
    END IF;

    IF v_TrangThaiDon = 'Cancelled' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong the thanh toan don hang da huy';
    END IF;

    -- Kiem tra chua thanh toan
    IF EXISTS (
        SELECT 1 FROM ThanhToan WHERE MaDonHang = p_MaDonHang AND TrangThai = 'Paid'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Don hang nay da duoc thanh toan';
    END IF;

    -- Upsert thanh toan
    INSERT INTO ThanhToan (MaDonHang, PhuongThuc, TrangThai, NgayThanhToan)
    VALUES (p_MaDonHang, p_PhuongThuc, 'Paid', p_NgayThanhToan)
    ON DUPLICATE KEY UPDATE
        PhuongThuc     = p_PhuongThuc,
        TrangThai      = 'Paid',
        NgayThanhToan  = p_NgayThanhToan;

    -- Chuyen trang thai don hang → Delivered
    UPDATE DonHang SET TrangThai = 'Delivered'
    WHERE MaDonHang = p_MaDonHang;
END$$

-- 4. BAO CAO DOANH THU THEO KHOANG THOI GIAN
CREATE PROCEDURE BaoCaoDoanhThu (
    IN p_TuNgay DATE,
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
CREATE PROCEDURE HuyDonHang (
    IN p_MaDonHang INT
)
BEGIN
    DECLARE v_TrangThai VARCHAR(50);
    DECLARE v_MaKho     INT;

    SELECT TrangThai, MaKho INTO v_TrangThai, v_MaKho
    FROM DonHang WHERE MaDonHang = p_MaDonHang;

    IF v_TrangThai = 'Delivered' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong the huy don hang da giao';
    END IF;

    IF v_TrangThai = 'Cancelled' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Don hang nay da bi huy truoc do';
    END IF;

    -- Hoan tra ton kho cho tung san pham trong don hang
    UPDATE TonKho tk
    JOIN ChiTietDonHang ct ON tk.MaKho = v_MaKho AND tk.MaSanPham = ct.MaSanPham
    SET tk.SoLuong = tk.SoLuong + ct.SoLuong
    WHERE ct.MaDonHang = p_MaDonHang;

    -- Cap nhat trang thai
    UPDATE DonHang SET TrangThai = 'Cancelled' WHERE MaDonHang = p_MaDonHang;

    -- Cap nhat thanh toan neu co
    UPDATE ThanhToan SET TrangThai = 'Refunded'
    WHERE MaDonHang = p_MaDonHang;
END$$

DELIMITER ;
