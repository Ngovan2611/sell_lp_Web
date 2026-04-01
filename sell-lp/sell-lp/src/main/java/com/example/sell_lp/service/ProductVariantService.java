package com.example.sell_lp.service;


import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.mapper.ProductVariantResponseMapper;
import com.example.sell_lp.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {

    ProductVariantRepository productVariantRepository;

    ProductVariantResponseMapper productVariantResponseMapper;

    public List<ProductVariantResponse> getAllProductVariantById(Long productId) {
        List<ProductVariant> productVariants = productVariantRepository.findByProduct_ProductId(productId);
        return productVariants.stream()
                .map(productVariantResponseMapper::toProductVariantResponse).toList();
    }


    public ProductVariant getVariantEntityById(Long variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }
}
