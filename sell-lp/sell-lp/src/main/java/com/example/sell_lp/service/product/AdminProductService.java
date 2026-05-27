package com.example.sell_lp.service.product;

import com.example.sell_lp.dto.request.product.ProductRequest;
import com.example.sell_lp.dto.request.product.ProductUpdateRequest;
import com.example.sell_lp.dto.request.product.ProductVariantUpdateRequest;
import com.example.sell_lp.dto.request.product.TagRequest;
import com.example.sell_lp.dto.response.product.ProductResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.entity.ProductImage;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.entity.Tag;
import com.example.sell_lp.mapper.ProductMapper;
import com.example.sell_lp.mapper.ProductVariantResponseMapper;
import com.example.sell_lp.repository.product.ProductRepository;
import com.example.sell_lp.repository.product.ProductVariantRepository;
import com.example.sell_lp.repository.variantRepository.ColorRepository;
import com.example.sell_lp.repository.variantRepository.RamRepository;
import com.example.sell_lp.repository.variantRepository.RomRepository;
import com.example.sell_lp.service.category.CategoryService;
import com.example.sell_lp.service.variant.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class AdminProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductVariantResponseMapper productVariantResponseMapper;
    ProductVariantRepository productVariantRepository;
    RamRepository ramRepository;
    ColorRepository colorRepository;
    RomRepository romRepository;
    CategoryService categoryService;
    ProductVariantService productVariantService;
    TagService tagService;
    ProductPageService productPageService;
    public void createProduct(ProductRequest request) {
        Category category = categoryService.getById(request.getCategoryId());

        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setActive(true);

        // =========================================================================
        // LOGIC 1: XỬ LÝ TỰ ĐỘNG TẠO MỚI/GẮN TAG KHI TẠO MỚI SẢN PHẨM
        // =========================================================================
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> productTags = processTags(request.getTagIds());
            product.setTags(productTags);
        }

        Product savedProduct = productRepository.saveAndFlush(product);

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            ProductImage mainImg = new ProductImage();
            mainImg.setUrl(request.getImageUrl());
            mainImg.setPrimary(true);
            mainImg.setProduct(product);
            product.getImages().clear();
            product.getImages().add(mainImg);
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Sử dụng Set để lưu trữ các chuỗi đại diện cho bộ thuộc tính độc nhất: color_ram_rom
            Set<String> uniqueVariantKeys = new HashSet<>();

            List<ProductVariant> variants = request.getVariants().stream()
                    .map(vReq -> {
                        // 1. Kiểm tra trùng lặp biến thể trong cùng một Request đầu vào
                        String variantKey = String.format("%s_%s_%s",
                                vReq.getColorId(), vReq.getRamId(), vReq.getRomId());

                        if (!uniqueVariantKeys.add(variantKey)) {
                            throw new RuntimeException("Cấu hình sản phẩm (Màu sắc, RAM, ROM) này đã tồn tại trong danh sách thêm mới!");
                        }

                        // 2. Map dữ liệu cơ bản
                        ProductVariant variant = productVariantResponseMapper.toProductVariant(vReq);
                        variant.setProduct(savedProduct);

                        // 3. Validate giá cả và số lượng số âm
                        if (vReq.getPrice() == null || vReq.getPrice() < 0) {
                            throw new RuntimeException("Giá bán không hợp lệ (phải từ 0 trở lên)");
                        }
                        if (vReq.getStockQty() == null || vReq.getStockQty() < 0) {
                            throw new RuntimeException("Số lượng kho không hợp lệ (phải từ 0 trở lên)");
                        }

                        // 4. Liên kết các Entity thuộc tính
                        if (vReq.getColorId() != null) {
                            variant.setColor(colorRepository.getReferenceById(vReq.getColorId()));
                        }
                        if (vReq.getRamId() != null) {
                            variant.setRam(ramRepository.getReferenceById(vReq.getRamId()));
                        }
                        if (vReq.getRomId() != null) {
                            variant.setRom(romRepository.getReferenceById(vReq.getRomId()));
                        }
                        return variant;
                    })
                    .toList();

            productVariantRepository.saveAll(variants);
        }
    }

    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setActive(request.isActive());

        // =========================================================================
        // LOGIC 2: XỬ LÝ CẬP NHẬT/THAY ĐỔI VÀ TẠO MỚI TAG CHO SẢN PHẨM
        // =========================================================================
        if (request.getTagIds() != null) {
            if (request.getTagIds().isEmpty()) {
                product.getTags().clear();
            } else {
                Set<Tag> updatedTags = processTags(request.getTagIds());
                product.setTags(updatedTags);
            }
        }

        if (request.getImageUrls() != null) {
            product.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String url = request.getImageUrls().get(i);
                if (url != null && !url.trim().isEmpty()) {
                    ProductImage img = new ProductImage();
                    img.setUrl(url.trim());
                    img.setProduct(product);
                    img.setPrimary(i == 0);
                    product.getImages().add(img);
                }
            }
        }

        if (request.getCategoryId() != null) {
            Category category = categoryService.getById(request.getCategoryId());
            product.setCategory(category);
        }

        if (request.getVariants() != null) {
            for (ProductVariantUpdateRequest vRequest : request.getVariants()) {
                ProductVariant variant = productVariantService.getVariantEntityById(vRequest.getVariantId());
                if (vRequest.getPrice() == null || vRequest.getPrice() < 0) {
                    throw new RuntimeException("Giá bán cập nhật không hợp lệ");
                }
                if (vRequest.getStockQty() == null || vRequest.getStockQty() < 0) {
                    throw new RuntimeException("Số lượng kho cập nhật không hợp lệ");
                }
                variant.setPrice(vRequest.getPrice());
                variant.setStockQty(vRequest.getStockQty());
            }
        }

        return productMapper.productToProductResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAdminProducts(
            String keyword, Integer categoryId, String stock,
            Boolean active, String tagSlug, String sort, Pageable pageable
    ) {
        String cleanKeyword = productPageService.cleanString(keyword);
        String cleanStock = productPageService.cleanString(stock);
        String cleanTag = productPageService.cleanString(tagSlug);
        String currentSort = (sort != null) ? sort : "default";

        Page<Product> productPage = switch (currentSort) {
            case "price_asc" -> productRepository.adminSearchPriceAsc(cleanKeyword, categoryId, cleanStock, active, cleanTag, pageable);
            case "price_desc" -> productRepository.adminSearchPriceDesc(cleanKeyword, categoryId, cleanStock, active, cleanTag, pageable);
            case "name_asc" -> productRepository.adminSearchNameAsc(cleanKeyword, categoryId, cleanStock, active, cleanTag, pageable);
            default -> productRepository.adminSearchNewest(cleanKeyword, categoryId, cleanStock, active, cleanTag, pageable);
        };

        return productPage.map(productMapper::productToProductResponse);
    }

    public Long countLowStockProducts() {
        return (long) productRepository.findLowStockProducts().size();
    }

    private Set<Tag> processTags(List<TagRequest> tagRequests) {
        Set<Tag> tags = new HashSet<>();
        if (tagRequests == null || tagRequests.isEmpty()) {
            return tags;
        }

        for (TagRequest tagItem : tagRequests) {
            // Trường hợp 1: Chọn tag đã có sẵn qua ID
            if (tagItem.getTagId() != null) {
                // Ép kiểu chuẩn xác từ Long sang Integer nếu cần thiết theo service của bạn
                Tag existingTag = tagService.getTagById((long) tagItem.getTagId().intValue());
                if (existingTag != null) {
                    tags.add(existingTag);
                }
                // Trường hợp 2: Nhập tên để tự động tìm hoặc tạo nhãn mới
            } else if (tagItem.getName() != null && !tagItem.getName().trim().isEmpty()) {
                String tagName = tagItem.getName().trim();
                Tag tag = tagService.findOrCreateTag(tagName);
                tags.add(tag);
            }
        }
        return tags;
    }
}