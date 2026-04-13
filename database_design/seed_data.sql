-- ============================================================
-- SEED DATA — MOCK DATA FOR TESTING ALL APIs
-- ============================================================
-- CACH SU DUNG:
--
--   BUOC 1: Tao tai khoan qua API (backend phai dang chay)
--   -------------------------------------------------------
--   Goi 5 request POST trong Swagger http://localhost:8080/swagger-ui.html
--   hoac dung cURL / Postman:
--
--   [ADMIN 1]
--   POST /api/auth/register/admin
--   { "ten": "Nguyen Van Admin", "email": "admin@warehouse.com", "matKhau": "Admin@123" }
--
--   [ADMIN 2]
--   POST /api/auth/register/admin
--   { "ten": "Tran Thi Manager", "email": "manager@warehouse.com", "matKhau": "Admin@123" }
--
--   [CUSTOMER 1]
--   POST /api/auth/register
--   { "ten": "Hoang Cong Minh", "email": "minh@customer.com", "matKhau": "Customer@123", "dienThoai": "0901234567", "diaChi": "12 Nguyen Hue, Q1, TP.HCM" }
--
--   [CUSTOMER 2]
--   POST /api/auth/register
--   { "ten": "Pham Thi Hoa", "email": "hoa@customer.com", "matKhau": "Customer@123", "dienThoai": "0912345678", "diaChi": "45 Le Loi, Q3, TP.HCM" }
--
--   [CUSTOMER 3]
--   POST /api/auth/register
--   { "ten": "Le Van Thanh", "email": "thanh@customer.com", "matKhau": "Customer@123", "dienThoai": "0923456789", "diaChi": "78 Tran Hung Dao, Q5, TP.HCM" }
--
--   BUOC 2: Chay phan SQL ben duoi trong MySQL Workbench
--   -------------------------------------------------------
--
-- TAI KHOAN SAU KHI SEED:
--   ADMIN    : admin@warehouse.com    / Admin@123
--   ADMIN    : manager@warehouse.com  / Admin@123
--   CUSTOMER : minh@customer.com      / Customer@123
--   CUSTOMER : hoa@customer.com       / Customer@123
--   CUSTOMER : thanh@customer.com     / Customer@123
-- ============================================================

USE WarehouseRentalDB;

-- Kiem tra accounts da ton tai chua
SELECT 'CHECK ACCOUNTS:' AS '';
SELECT MaAdmin, TenAdmin, Email FROM Admin;
SELECT MaKhachHang, TenKhachHang, Email FROM KhachHang;

-- ============================================================
-- MASTER DATA
-- ============================================================

-- KHO (3 kho)
INSERT INTO Kho (MaKho, TenKho, DiaChi, DienTich, TrangThai, MaAdmin) VALUES
(1, 'Kho A - Binh Duong', 'KCN Vsip, Binh Duong',          1200.5, 'Active',   1),
(2, 'Kho B - Long An',    'KCN Tan Duc, Long An',           800.0,  'Active',   1),
(3, 'Kho C - Dong Nai',   'KCN Amata, Bien Hoa, Dong Nai',  600.0,  'Inactive', 2);

-- HOP DONG THUE KHO
INSERT INTO HopDongThueKho (MaHopDong, MaKho, MaKhachHang, NgayBatDau, NgayKetThuc, GiaThue) VALUES
(1, 1, 1, '2025-01-01', '2025-12-31', 50000000.00),
(2, 2, 2, '2025-03-01', '2026-02-28', 35000000.00);

-- DANH MUC SAN PHAM
INSERT INTO DanhMuc (MaDanhMuc, TenDanhMuc) VALUES
(1, 'Dien Tu'),
(2, 'May Mac'),
(3, 'Thuc Pham'),
(4, 'Hoa Chat Cong Nghiep');

-- NHA CUNG CAP
INSERT INTO NhaCungCap (MaNCC, TenNCC, DienThoai, DiaChi) VALUES
(1, 'Cong Ty TNHH Dien Tu ABC', '028-3812-3456', '123 Nguyen Van Linh, Q7, TP.HCM'),
(2, 'Cong Ty May Mac XYZ',      '028-3921-4567', '456 Cong Hoa, Tan Binh, TP.HCM'),
(3, 'Cong Ty Thuc Pham DEF',    '028-3756-7890', '789 Ly Thuong Kiet, Q10, TP.HCM');

-- SAN PHAM (6 san pham thuoc cac khach hang)
INSERT INTO SanPham (MaSanPham, TenSanPham, GiaBan, MaDanhMuc, MaKhachHang) VALUES
(1, 'Laptop Dell XPS 15',        25000000.00, 1, 1),
(2, 'Dien Thoai Samsung S24',    18000000.00, 1, 1),
(3, 'Ao Thun Cotton Nam',           250000.00, 2, 2),
(4, 'Quan Jean Nu',                 450000.00, 2, 2),
(5, 'Gao Nep Thuong Hang 5kg',      120000.00, 3, 3),
(6, 'Dau An Tuong An 2L',            85000.00, 3, 3);

-- ============================================================
-- NHAP HANG — trigger trg_tang_tonkho tu dong cap nhat TonKho
-- ============================================================

-- Phieu 1: Kho A nhap Dien Tu
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaKho, MaNCC) VALUES (1, '2025-10-01', 1, 1);
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(1, 1,  50, 20000000.00),   -- TonKho(Kho1, SP1) = 50
(1, 2, 100, 14000000.00);   -- TonKho(Kho1, SP2) = 100

-- Phieu 2: Kho B nhap May Mac
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaKho, MaNCC) VALUES (2, '2025-10-05', 2, 2);
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(2, 3, 200, 180000.00),     -- TonKho(Kho2, SP3) = 200
(2, 4, 150, 320000.00);     -- TonKho(Kho2, SP4) = 150

-- Phieu 3: Kho B nhap Thuc Pham
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaKho, MaNCC) VALUES (3, '2025-10-10', 2, 3);
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(3, 5, 500, 95000.00),      -- TonKho(Kho2, SP5) = 500
(3, 6, 300, 65000.00);      -- TonKho(Kho2, SP6) = 300

-- ============================================================
-- DON HANG + THANH TOAN
-- trigger trg_giam_tonkho tu dong tru TonKho khi INSERT ChiTietDonHang
--
-- Don 1: Delivered + Paid   (Kho A, KH1) — 11/2025
-- Don 2: Processing          (Kho A, KH1) — 12/2025
-- Don 3: Pending             (Kho B, KH2) — 01/2026
-- Don 4: Cancelled           (Kho B, KH3) — 02/2026 (hoan tra ton kho)
-- Don 5: Pending moi         (Kho A, KH2) — de test thanh toan qua Swagger
-- ============================================================

-- Don 1: Delivered + Paid
INSERT INTO DonHang (MaDonHang, NgayLap, MaKho, MaKhachHang, TrangThai) VALUES
(1, '2025-11-10', 1, 1, 'Delivered');
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES
(1, 1, 2, 25000000.00),   -- Kho1,SP1: 50->48
(1, 2, 3, 18000000.00);   -- Kho1,SP2: 100->97
INSERT INTO ThanhToan (MaDonHang, PhuongThuc, TrangThai, NgayThanhToan) VALUES
(1, 'BankTransfer', 'Paid', '2025-11-11');

-- Don 2: Processing (chua thanh toan)
INSERT INTO DonHang (MaDonHang, NgayLap, MaKho, MaKhachHang, TrangThai) VALUES
(2, '2025-12-01', 1, 1, 'Processing');
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES
(2, 1,  5, 25000000.00),  -- Kho1,SP1: 48->43
(2, 2, 10, 18000000.00);  -- Kho1,SP2: 97->87

-- Don 3: Pending (chua thanh toan)
INSERT INTO DonHang (MaDonHang, NgayLap, MaKho, MaKhachHang, TrangThai) VALUES
(3, '2026-01-15', 2, 2, 'Pending');
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES
(3, 3, 20, 250000.00),    -- Kho2,SP3: 200->180
(3, 4, 10, 450000.00);    -- Kho2,SP4: 150->140

-- Don 4: se bi huy -> hoan tra ton kho
INSERT INTO DonHang (MaDonHang, NgayLap, MaKho, MaKhachHang, TrangThai) VALUES
(4, '2026-02-01', 2, 3, 'Pending');
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES
(4, 5, 50, 120000.00),    -- Kho2,SP5: 500->450
(4, 6, 30,  85000.00);    -- Kho2,SP6: 300->270
CALL HuyDonHang(4);       -- Cancelled + hoan tra ton kho

-- Don 5: Pending moi — de test POST /api/donhang va POST /api/thanhtoan
INSERT INTO DonHang (MaDonHang, NgayLap, MaKho, MaKhachHang, TrangThai) VALUES
(5, '2026-02-20', 1, 2, 'Pending');
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES
(5, 1, 1, 25000000.00),   -- Kho1,SP1: 43->42
(5, 2, 2, 18000000.00);   -- Kho1,SP2: 87->85

-- ============================================================
-- VERIFY — Kiem tra ket qua sau khi seed
-- ============================================================
SELECT '===== TON KHO (ket qua cuoi) =====' AS '';
SELECT tk.MaKho, k.TenKho, tk.MaSanPham, sp.TenSanPham, tk.SoLuong
FROM TonKho tk
JOIN Kho k      ON tk.MaKho     = k.MaKho
JOIN SanPham sp ON tk.MaSanPham = sp.MaSanPham
ORDER BY tk.MaKho, tk.MaSanPham;

-- Ton kho mong doi sau khi seed:
-- Kho A: SP1(Laptop)=42, SP2(Samsung)=85
-- Kho B: SP3(AoThun)=180, SP4(QuanJean)=140, SP5(Gao)=500, SP6(Dau)=300
-- (Don4 bi huy nen SP5,SP6 duoc hoan tra ve 500,300)

SELECT '===== DON HANG =====' AS '';
SELECT dh.MaDonHang, dh.NgayLap, dh.TrangThai, kh.TenKhachHang, k.TenKho
FROM DonHang dh
JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang
JOIN Kho k        ON dh.MaKho       = k.MaKho;

SELECT '===== THANH TOAN =====' AS '';
SELECT * FROM ThanhToan;

SELECT '===== VIEW: TONG HOA DON =====' AS '';
SELECT * FROM View_TongHoaDon;

SELECT '===== VIEW: DOANH THU THEO THANG =====' AS '';
SELECT * FROM View_DoanhThuThang;

SELECT '===== VIEW: TOP SAN PHAM BAN CHAY =====' AS '';
SELECT * FROM View_TopSanPham;

