package com.example.sell_lp.service.product;

import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.mapper.ProductMapper;
import com.example.sell_lp.repository.ProductRepository;
import com.example.sell_lp.service.category.CategoryService;
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
    CategoryService categoryService;

    public List<ProductResponse> getAllProductsByCategoryDemo(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryIdDemo(categoryId);

        return products
                .stream()
                .map(productMapper::productToProductResponse)
                .toList();

    }
    public Page<ProductResponse> getProductsFiltered(Integer categoryId, String keyword, String sort, Pageable pageable) {
        Page<Product> productPage;


            if ("price_asc".equals(sort)) {
                productPage = productRepository.searchProductsOrderByPriceAsc(keyword, categoryId, pageable);
            } else if ("price_desc".equals(sort)) {
                productPage = productRepository.searchProductsOrderByPriceDesc(keyword, categoryId, pageable);
            } else {
                productPage = productRepository.searchProducts(keyword, categoryId, pageable);
            }


        return productPage.map(productMapper::productToProductResponse);
    }


    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findByProductId(productId);
        return productMapper.productToProductResponse(product);
    }

    public List<ProductResponse> getRelatedProducts(Integer categoryId, Long id, Pageable pageable) {
        Category category = categoryService.getById(categoryId);
        List<Product> products = productRepository.findRelatedProducts(category, id, pageable);
        return products.stream().map(productMapper::productToProductResponse).toList();
    }

}

