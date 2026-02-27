package com.example.warehousedb.service;

import com.example.warehousedb.dto.response.BaoCaoDoanhThuResponse;
import com.example.warehousedb.dto.response.DonHangResponse;
import com.example.warehousedb.repository.DonHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaoCaoService {

    private final DonHangRepository donHangRepository;
    private final DonHangService    donHangService;

    // ── DOANH THU THEO THANG ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuResponse> getDoanhThuTheoThang() {
        return donHangRepository.findAll().stream()
                .filter(dh -> !"Cancelled".equals(dh.getTrangThai()))
                .filter(dh -> dh.getChiTietList() != null && !dh.getChiTietList().isEmpty())
                .collect(Collectors.groupingBy(
                        dh -> Map.entry(dh.getNgayLap().getYear(), dh.getNgayLap().getMonthValue())
                ))
                .entrySet().stream()
                .map(entry -> {
                    int nam   = entry.getKey().getKey();
                    int thang = entry.getKey().getValue();
                    long soDon = entry.getValue().size();
                    BigDecimal tong = entry.getValue().stream()
                            .flatMap(dh -> dh.getChiTietList().stream())
                            .map(ct -> ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return BaoCaoDoanhThuResponse.builder()
                            .nam(nam).thang(thang).soDonHang(soDon).tongDoanhThu(tong).build();
                })
                .sorted((a, b) -> {
                    int c = b.getNam().compareTo(a.getNam());
                    return c != 0 ? c : b.getThang().compareTo(a.getThang());
                })
                .collect(Collectors.toList());
    }

    // ── DON HANG THEO KHOANG NGAY ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DonHangResponse> getDonHangTheoKhoang(LocalDate tuNgay, LocalDate denNgay) {
        return donHangRepository.findAll().stream()
                .filter(dh -> !"Cancelled".equals(dh.getTrangThai()))
                .filter(dh -> {
                    LocalDate ngay = dh.getNgayLap();
                    return !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay);
                })
                .map(donHangService::toResponse)
                .collect(Collectors.toList());
    }
}
