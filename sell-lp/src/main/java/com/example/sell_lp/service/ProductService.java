package com.example.sell_lp.service;


import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.mapper.ProductMapper;
import com.example.sell_lp.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;



@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductService {
    ProductRepository productRepository;

    ProductMapper productMapper;

    public List<ProductResponse> getAllProductsByCategoryDemo(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryIdDemo(categoryId);

        return products
                .stream()
                .map(productMapper::productToProductResponse)
                .toList();

    }
    public Page<ProductResponse> getProductsFiltered(Integer categoryId, String sort, Pageable pageable) {
        Page<Product> productPage;

        if (categoryId == null) {
            if ("price_asc".equals(sort)) {
                productPage = productRepository.findAllOrderByMinPriceAsc(pageable);
            } else if ("price_desc".equals(sort)) {
                productPage = productRepository.findAllOrderByMinPriceDesc(pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }
        } else {
            if ("price_asc".equals(sort)) {
                productPage = productRepository.findAllByCategoryIdOrderByMinPriceAsc(categoryId, pageable);
            } else if ("price_desc".equals(sort)) {
                productPage = productRepository.findAllByCategoryIdOrderByMinPriceDesc(categoryId, pageable);
            } else {
                productPage = productRepository.findByCategoryCategoryId(categoryId, pageable);
            }
        }

        return productPage.map(productMapper::productToProductResponse);
    }
    public Page<ProductResponse> getAllProductsByCategory(Integer categoryId, Pageable pageable) {
        Page<Product> productPage =
                productRepository.findByCategoryCategoryId(categoryId, pageable);

        return productPage.map(productMapper::productToProductResponse);
    }
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::productToProductResponse);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findByProductId(productId);
        return productMapper.productToProductResponse(product);
    }

    public List<ProductResponse> getRelatedProducts(Category category, Long id, Pageable pageable) {
        List<Product> products = productRepository.findRelatedProducts(category, id, pageable);
        return products.stream().map(productMapper::productToProductResponse).toList();
    }

}
