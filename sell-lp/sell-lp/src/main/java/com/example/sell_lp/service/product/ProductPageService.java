package com.example.sell_lp.service.product;



import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductPageService {

    // Tách chuỗi rỗng về null gọn gàng
    public String cleanString(String input) {
        return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
    }

    // Tự động gộp sort từ chuỗi request vào Pageable để triệt tiêu switch-case ở Service
    public static Pageable applySort(Pageable pageable, String sort) {
        if (sort == null) return pageable;

        Sort springSort = switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "variants.price"); // Thay bằng field tương ứng trong Entity của bạn
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "variants.price");
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // Mặc định mới nhất
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), springSort);
    }
}