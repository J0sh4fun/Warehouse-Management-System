package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.PhieuNhapRequest;
import com.example.warehousedb.dto.response.PhieuNhapResponse;
import com.example.warehousedb.entity.Kho;
import com.example.warehousedb.entity.NhaCungCap;
import com.example.warehousedb.entity.SanPham;
import com.example.warehousedb.entity.TonKho;
import com.example.warehousedb.entity.TonKhoId;
import com.example.warehousedb.repository.KhoRepository;
import com.example.warehousedb.repository.NhaCungCapRepository;
import com.example.warehousedb.repository.SanPhamRepository;
import com.example.warehousedb.repository.TonKhoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PhieuNhapServiceIntegrationTest {

    @Autowired
    private PhieuNhapService phieuNhapService;

    @Autowired
    private KhoRepository khoRepository;

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private TonKhoRepository tonKhoRepository;

    @Test
    void taoPhieuNhapTangTonKho() {
        Kho kho = khoRepository.findAll().stream().findFirst().orElseThrow();
        NhaCungCap ncc = nhaCungCapRepository.findAll().stream().findFirst().orElseThrow();
        SanPham sanPham = sanPhamRepository.findAll().stream().findFirst().orElseThrow();

        int soLuongNhap = 7;
        int tonBanDau = getSoLuongTon(kho.getMaKho(), sanPham.getMaSanPham());

        PhieuNhapResponse response = phieuNhapService.taoPhieuNhap(
                buildRequest(kho.getMaKho(), ncc.getMaNCC(), sanPham.getMaSanPham(), soLuongNhap)
        );

        int tonSauNhap = getSoLuongTon(kho.getMaKho(), sanPham.getMaSanPham());

        assertThat(response).isNotNull();
        assertThat(tonSauNhap).isEqualTo(tonBanDau + soLuongNhap);
    }

    @Test
    void xoaPhieuNhapHoanTonKho() {
        Kho kho = khoRepository.findAll().stream().findFirst().orElseThrow();
        NhaCungCap ncc = nhaCungCapRepository.findAll().stream().findFirst().orElseThrow();
        SanPham sanPham = sanPhamRepository.findAll().stream().findFirst().orElseThrow();

        int soLuongNhap = 5;
        int tonBanDau = getSoLuongTon(kho.getMaKho(), sanPham.getMaSanPham());

        PhieuNhapResponse response = phieuNhapService.taoPhieuNhap(
                buildRequest(kho.getMaKho(), ncc.getMaNCC(), sanPham.getMaSanPham(), soLuongNhap)
        );
        phieuNhapService.deletePhieuNhap(response.getMaPhieuNhap());

        int tonSauXoa = getSoLuongTon(kho.getMaKho(), sanPham.getMaSanPham());

        assertThat(tonSauXoa).isEqualTo(tonBanDau);
    }

    private int getSoLuongTon(Integer maKho, Integer maSanPham) {
        return tonKhoRepository.findById(new TonKhoId(maKho, maSanPham))
                .map(TonKho::getSoLuong)
                .orElse(0);
    }

    private PhieuNhapRequest buildRequest(Integer maKho, Integer maNCC, Integer maSanPham, int soLuong) {
        PhieuNhapRequest.ChiTietRequest chiTietRequest = PhieuNhapRequest.ChiTietRequest.builder()
                .maSanPham(maSanPham)
                .soLuong(soLuong)
                .giaNhap(BigDecimal.valueOf(100))
                .build();

        return PhieuNhapRequest.builder()
                .maKho(maKho)
                .maNCC(maNCC)
                .chiTiet(List.of(chiTietRequest))
                .build();
    }
}
