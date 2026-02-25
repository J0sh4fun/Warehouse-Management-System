Warehouse Rental Management System 

I. FLOW PHÁT TRIỂN DỰ ÁN (TEAM WORKFLOW)

Giai đoạn 1 – Phân tích & Thiết kế

Phân tích yêu cầu nghiệp vụ

Xác định Actors: Admin – Customer

Thiết kế ERD

Chuẩn hóa 3NF

Thiết kế DB (MySQL)

Giai đoạn 2 – Xây dựng Database

Tạo bảng (CREATE)

Thêm khóa chính – khóa ngoại

Tạo Trigger (tăng/giảm tồn kho)

Tạo View (báo cáo)

Tạo Stored Procedure

Giai đoạn 3 – Xây dựng Backend (Spring Boot)

Flow làm backend:

Entity → Repository → Service → Controller → API Test (Postman)

Thứ tự nên làm:

Tạo Entity mapping DB

Tạo Repository (JPA)

Viết Service (business logic)

Viết Controller (REST API)

Test API bằng Postman

Thêm Security (JWT)

Thêm DTO & Validation

Giai đoạn 4 – Frontend

Thiết kế UI (Admin / Customer)

Kết nối API

Xử lý token & phân quyền

Hoàn thiện dashboard & báo cáo

II. FLOW XỬ LÝ REQUEST (BACKEND FLOW)

Ví dụ: Khách hàng tạo đơn hàng

Frontend gửi request POST /api/orders -> Controller nhận request -> DTO validate dữ liệu -> Service xử lý nghiệp vụ -> Repository lưu vào DB -> Trigger giảm tồn kho -> Trả response về client

III. FLOW NGHIỆP VỤ TOÀN HỆ THỐNG

- Thuê kho

Admin tạo kho -> Customer ký hợp đồng thuê -> Hệ thống lưu HopDongThueKho -> Customer được phép sử dụng kho

- Nhập hàng

Customer tạo Phiếu nhập -> Thêm ChiTietPhieuNhap -> Trigger tự động tăng TonKho

- Bán hàng

Customer tạo Đơn hàng -> Thêm ChiTietDonHang -> Trigger kiểm tra tồn kho -> (Nếu đủ → trừ tồn hoặc Nếu thiếu → báo lỗi)

- Thanh toán

Customer tạo ThanhToan -> Cập nhật trạng thái đơn hàng -> View tính doanh thu

IV. FLOW SECURITY (JWT)

Login -> Spring Security authenticate -> Tạo JWT Token -> Frontend lưu token -> Mỗi request kèm Authorization header

Conclusion: Hệ thống được triển khai theo quy trình: phân tích → thiết kế ERD → chuẩn hóa → xây dựng DB → phát triển backend theo kiến trúc 3-layer → tích hợp frontend → kiểm thử và bảo mật bằng JWT.
