CREATE DATABASE WarehouseRentalDB;
USE WarehouseRentalDB;

-- ADMIN
CREATE TABLE Admin (
    MaAdmin INT AUTO_INCREMENT PRIMARY KEY,
    TenAdmin VARCHAR(100),
    Email VARCHAR(100) UNIQUE,
    MatKhau VARCHAR(255)
);

-- KHACH HANG
CREATE TABLE KhachHang (
    MaKhachHang INT AUTO_INCREMENT PRIMARY KEY,
    TenKhachHang VARCHAR(100),
    Email VARCHAR(100) UNIQUE,
    DienThoai VARCHAR(20),
    DiaChi VARCHAR(255)
);

-- KHO
CREATE TABLE Kho (
    MaKho INT AUTO_INCREMENT PRIMARY KEY,
    TenKho VARCHAR(100),
    DiaChi VARCHAR(255),
    DienTich FLOAT,
    TrangThai VARCHAR(50),
    MaAdmin INT,
    FOREIGN KEY (MaAdmin) REFERENCES Admin(MaAdmin)
);

-- HOP DONG
CREATE TABLE HopDongThueKho (
    MaHopDong INT AUTO_INCREMENT PRIMARY KEY,
    MaKho INT,
    MaKhachHang INT,
    NgayBatDau DATE,
    NgayKetThuc DATE,
    GiaThue DECIMAL(12,2),
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang)
);

-- DANH MUC
CREATE TABLE DanhMuc (
    MaDanhMuc INT AUTO_INCREMENT PRIMARY KEY,
    TenDanhMuc VARCHAR(100)
);

-- SAN PHAM
CREATE TABLE SanPham (
    MaSanPham INT AUTO_INCREMENT PRIMARY KEY,
    TenSanPham VARCHAR(100),
    GiaBan DECIMAL(12,2),
    MaDanhMuc INT,
    MaKhachHang INT,
    FOREIGN KEY (MaDanhMuc) REFERENCES DanhMuc(MaDanhMuc),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang)
);

-- TON KHO
CREATE TABLE TonKho (
    MaKho INT,
    MaSanPham INT,
    SoLuong INT,
    PRIMARY KEY (MaKho, MaSanPham),
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- NHA CUNG CAP
CREATE TABLE NhaCungCap (
    MaNCC INT AUTO_INCREMENT PRIMARY KEY,
    TenNCC VARCHAR(100),
    DienThoai VARCHAR(20),
    DiaChi VARCHAR(255)
);

-- PHIEU NHAP
CREATE TABLE PhieuNhap (
    MaPhieuNhap INT AUTO_INCREMENT PRIMARY KEY,
    NgayNhap DATE,
    MaKho INT,
    MaNCC INT,
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho),
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
);

-- CT PHIEU NHAP
CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap INT,
    MaSanPham INT,
    SoLuong INT,
    GiaNhap DECIMAL(12,2),
    PRIMARY KEY (MaPhieuNhap, MaSanPham),
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- DON HANG
CREATE TABLE DonHang (
    MaDonHang INT AUTO_INCREMENT PRIMARY KEY,
    NgayLap DATE,
    MaKho INT,
    MaKhachHang INT,
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang)
);

-- CT DON HANG
CREATE TABLE ChiTietDonHang (
    MaDonHang INT,
    MaSanPham INT,
    SoLuong INT,
    DonGia DECIMAL(12,2),
    PRIMARY KEY (MaDonHang, MaSanPham),
    FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- THANH TOAN
CREATE TABLE ThanhToan (
    MaThanhToan INT AUTO_INCREMENT PRIMARY KEY,
    MaDonHang INT UNIQUE,
    PhuongThuc VARCHAR(50),
    TrangThai VARCHAR(50),
    NgayThanhToan DATE,
    FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang)
);
