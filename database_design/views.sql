-- ============================================================
-- VIEWS - BAO CAO & THONG KE (Sale & Payment Module)
-- ============================================================

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
    SUM(ct.SoLuong)            AS TongSoLuongBan,
    SUM(ct.SoLuong * ct.DonGia) AS TongDoanhThu
FROM ChiTietDonHang ct
JOIN SanPham   sp ON ct.MaSanPham  = sp.MaSanPham
JOIN DanhMuc   dm ON sp.MaDanhMuc  = dm.MaDanhMuc
JOIN DonHang   dh ON ct.MaDonHang  = dh.MaDonHang
WHERE dh.TrangThai != 'Cancelled'
GROUP BY sp.MaSanPham, sp.TenSanPham, dm.TenDanhMuc
ORDER BY TongSoLuongBan DESC;

-- 3. CHI TIET DON HANG (joined view - de query frontend)
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

-- 4. TONG HOA DON (tong tien moi don hang)
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
