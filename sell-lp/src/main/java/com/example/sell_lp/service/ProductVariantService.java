package com.example.sell_lp.service;


import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.mapper.ProductVariantResponseMapper;
import com.example.sell_lp.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {
    @Autowired
    ProductVariantRepository productVariantRepository;
    @Autowired
    ProductVariantResponseMapper productVariantResponseMapper;

    public List<ProductVariantResponse> getAllProductVariantById(Long productId) {
        List<ProductVariant> productVariants = productVariantRepository.findByProduct_ProductId(productId);
        return productVariants.stream()
                .map(productVariantResponseMapper::toProductVariantResponse).toList();
    }

    public ProductVariantResponse getVariantById(Long variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId).orElse(null);
        return productVariantResponseMapper.toProductVariantResponse(productVariant);
    }

    public ProductVariant getVariantEntityById(Long variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }
}
