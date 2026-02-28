package com.example.warehousedb.service;

import com.example.warehousedb.dto.request.NhaCungCapRequest;
import com.example.warehousedb.dto.response.NhaCungCapResponse;
import com.example.warehousedb.entity.NhaCungCap;
import com.example.warehousedb.repository.NhaCungCapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NhaCungCapService {

    private final NhaCungCapRepository nhaCungCapRepository;

    // ── TAO NHA CUNG CAP ──────────────────────────────────────────────────
    public NhaCungCapResponse taoNhaCungCap(NhaCungCapRequest request) {
        NhaCungCap nhaCungCap = NhaCungCap.builder()
                .tenNCC(request.getTenNCC())
                .dienThoai(request.getDienThoai())
                .diaChi(request.getDiaChi())
                .build();
        nhaCungCapRepository.save(nhaCungCap);
        return toResponse(nhaCungCap);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<NhaCungCapResponse> getAllNhaCungCap() {
        return nhaCungCapRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public NhaCungCapResponse getNhaCungCapById(Integer maNCC) {
        return toResponse(nhaCungCapRepository.findById(maNCC)
                .orElseThrow(() -> new IllegalArgumentException("Nha cung cap khong ton tai: " + maNCC)));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    public NhaCungCapResponse updateNhaCungCap(Integer maNCC, NhaCungCapRequest request) {
        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(maNCC)
                .orElseThrow(() -> new IllegalArgumentException("Nha cung cap khong ton tai: " + maNCC));

        nhaCungCap.setTenNCC(request.getTenNCC());
        nhaCungCap.setDienThoai(request.getDienThoai());
        nhaCungCap.setDiaChi(request.getDiaChi());
        nhaCungCapRepository.save(nhaCungCap);
        return toResponse(nhaCungCap);
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public void deleteNhaCungCap(Integer maNCC) {
        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(maNCC)
                .orElseThrow(() -> new IllegalArgumentException("Nha cung cap khong ton tai: " + maNCC));

        if (!nhaCungCap.getPhieuNhapList().isEmpty()) {
            throw new IllegalArgumentException("Khong the xoa nha cung cap co phieu nhap");
        }

        nhaCungCapRepository.delete(nhaCungCap);
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private NhaCungCapResponse toResponse(NhaCungCap nhaCungCap) {
        return NhaCungCapResponse.builder()
                .maNCC(nhaCungCap.getMaNCC())
                .tenNCC(nhaCungCap.getTenNCC())
                .dienThoai(nhaCungCap.getDienThoai())
                .diaChi(nhaCungCap.getDiaChi())
                .soPhieuNhap(nhaCungCap.getPhieuNhapList() != null ? nhaCungCap.getPhieuNhapList().size() : 0)
                .build();
    }
}

