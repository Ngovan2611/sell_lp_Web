package com.example.sell_lp.service.product;


import com.example.sell_lp.entity.Tag;
import com.example.sell_lp.repository.product.TagRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TagService {
    TagRepository tagRepository;

    public List<Tag> getAllTagsById(List<Long> tagIds) {
        return tagRepository.findAllById(tagIds);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
    // Thêm vào trong TagService.java của bạn
    @Transactional
    public Tag findOrCreateTag(String name) {
        // 1. Thử tìm xem tag này đã được tạo trước đó trong DB chưa (không phân biệt hoa thường)
        // Giả định bạn có hàm findByNameIgnoreCase trong TagRepository
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(name);

        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        // 2. Nếu chưa có ai tạo nhãn này -> Tiến hành tạo mới hoàn toàn
        Tag newTag = new Tag();
        newTag.setName(name);
        newTag.setSlug(toSlug(name)); // Tạo slug tự động (ví dụ: "Áo Thun" -> "ao-thun")

        return tagRepository.save(newTag);
    }

    // Hàm convert tiếng Việt / ký tự đặc biệt sang slug đơn giản
    private String toSlug(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input.toLowerCase(), java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
    public Tag getTagById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }
}
