package com.example.sell_lp.service.product;


import com.example.sell_lp.dto.request.ProductRequest;
import com.example.sell_lp.dto.request.ProductUpdateRequest;
import com.example.sell_lp.dto.request.ProductVariantUpdateRequest;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.mapper.ProductMapper;
import com.example.sell_lp.mapper.ProductVariantResponseMapper;
import com.example.sell_lp.repository.ProductRepository;
import com.example.sell_lp.repository.ProductVariantRepository;
import com.example.sell_lp.repository.variantRepository.ColorRepository;
import com.example.sell_lp.repository.variantRepository.RamRepository;
import com.example.sell_lp.repository.variantRepository.RomRepository;
import com.example.sell_lp.service.category.CategoryService;
import com.example.sell_lp.service.variant.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public void createProduct(ProductRequest request) {

        Category category = categoryService.getById(request.getCategoryId());

        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setActive(true);
        Product savedProduct = productRepository.saveAndFlush(product);

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ProductVariant> variants = request.getVariants().stream()
                    .map(vReq -> {
                        ProductVariant variant = productVariantResponseMapper.toProductVariant(vReq);
                        variant.setProduct(savedProduct);

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
        product.setActive(request.isActive());

        if (request.getImageUrls() != null) {
            List<String> newImages = new ArrayList<>(request.getImageUrls());
        }

        if (request.getVariants() != null) {
            for (ProductVariantUpdateRequest vRequest : request.getVariants()) {
                ProductVariant variant = productVariantService.getVariantEntityById(vRequest.getVariantId());

                variant.setPrice(vRequest.getPrice());
                variant.setStockQty(vRequest.getStockQty());
            }
        }

        return productMapper.productToProductResponse(productRepository.save(product));
    }
}
