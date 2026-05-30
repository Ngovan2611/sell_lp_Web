package com.example.sell_lp.service.banner;

import com.example.sell_lp.dto.request.banner.BannerRequest;
import com.example.sell_lp.dto.response.banner.BannerResponse;
import com.example.sell_lp.entity.Banner;
import com.example.sell_lp.mapper.BannerMapper;
import com.example.sell_lp.repository.banner.BannerRepository;
import com.example.sell_lp.service.variant.ImageUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerService {

    BannerRepository bannerRepository;
    BannerMapper bannerMapper;
    ImageUploadService imageUploadService;

    // READ ALL: Lấy danh sách tất cả banner
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll()
                .stream()
                .map(bannerMapper::toBannerResponse)
                .collect(Collectors.toList());
    }

    // READ ONE: Lấy chi tiết 1 banner theo ID (Phục vụ cho việc đổ dữ liệu lên Form Sửa)
    public BannerResponse getBannerById(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Banner có ID: " + id));
        return bannerMapper.toBannerResponse(banner);
    }

    // CREATE: Thêm mới banner kèm upload ảnh lên Cloudinary
    @Transactional
    public BannerResponse saveBanner(BannerRequest bannerRequest) throws IOException {
        Banner banner = bannerMapper.toBanner(bannerRequest);

        if (bannerRequest.getImageFile() != null && !bannerRequest.getImageFile().isEmpty()) {
            String cloudImageUrl = imageUploadService.uploadImage(bannerRequest.getImageFile(), "banner");
            banner.setImageUrl(cloudImageUrl);
        }

        Banner savedBanner = bannerRepository.save(banner);
        return bannerMapper.toBannerResponse(savedBanner);
    }

    // UPDATE: Sửa thông tin banner cập nhật dữ liệu và thay ảnh nếu có
    @Transactional
    public BannerResponse updateBanner(Integer id, BannerRequest bannerRequest) throws IOException {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Banner có ID: " + id));

        // Cập nhật các trường thông tin cơ bản từ Request
        existingBanner.setTitle(bannerRequest.getTitle());
        existingBanner.setLinkUrl(bannerRequest.getLinkUrl());
        existingBanner.setActive(bannerRequest.isActive());

        // Kiểm tra nếu người dùng upload ảnh mới thì đè lên ảnh cũ
        if (bannerRequest.getImageFile() != null && !bannerRequest.getImageFile().isEmpty()) {
            String newCloudImageUrl = imageUploadService.uploadImage(bannerRequest.getImageFile(), "banner");
            existingBanner.setImageUrl(newCloudImageUrl);
        }
        // Nếu không chọn file mới, existingBanner vẫn giữ nguyên imageUrl cũ từ DB

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return bannerMapper.toBannerResponse(updatedBanner);
    }

    // TOGGLE: Bật/Tắt hiển thị nhanh ngoài giao diện
    @Transactional
    public void toggleActive(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Banner có ID: " + id));
        banner.setActive(!banner.isActive());
        bannerRepository.save(banner);
    }

    // DELETE: Xóa banner khỏi hệ thống
    @Transactional
    public void deleteBanner(Integer id) {
        if (!bannerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Banner có ID: " + id);
        }
        bannerRepository.deleteById(id);
    }
    // Thêm hàm này vào BannerService để phục vụ riêng cho trang User
    public List<BannerResponse> getActiveBannersForUser() {
        return bannerRepository.findAll()
                .stream()
                .filter(Banner::isActive) // 1. Chỉ lấy những banner đang bật hiển thị
                .map(bannerMapper::toBannerResponse) // 2. Map sang Response sạch
                .collect(Collectors.toList());
    }
}