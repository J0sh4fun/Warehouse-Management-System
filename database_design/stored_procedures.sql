-- XỬ LÝ NGHIỆP VỤ

DELIMITER $$

CREATE PROCEDURE TaoDonHang (
    IN p_MaKho INT,
    IN p_MaKhachHang INT,
    IN p_NgayLap DATE
)
BEGIN
    INSERT INTO DonHang (MaKho, MaKhachHang, NgayLap)
    VALUES (p_MaKho, p_MaKhachHang, p_NgayLap);
END$$

DELIMITER ;
