-- BÁO CÁO & THỐNG KÊ

-- DOANH THU THEO THANG
CREATE VIEW View_DoanhThuThang AS
SELECT 
    YEAR(NgayLap) AS Nam,
    MONTH(NgayLap) AS Thang,
    SUM(SoLuong * DonGia) AS TongDoanhThu
FROM DonHang dh
JOIN ChiTietDonHang ct ON dh.MaDonHang = ct.MaDonHang
GROUP BY YEAR(NgayLap), MONTH(NgayLap);

-- TOP SAN PHAM BAN CHAY
CREATE VIEW View_TopSanPham AS
SELECT 
    sp.TenSanPham,
    SUM(ct.SoLuong) AS TongBan
FROM ChiTietDonHang ct
JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham
GROUP BY sp.TenSanPham
ORDER BY TongBan DESC;
