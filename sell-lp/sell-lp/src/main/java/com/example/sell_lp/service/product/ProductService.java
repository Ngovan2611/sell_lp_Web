package com.example.sell_lp.service.product;

import com.example.sell_lp.dto.response.product.ProductResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.mapper.ProductMapper;
import com.example.sell_lp.repository.product.ProductRepository;
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
    ProductPageService productPageService;
    public List<ProductResponse> getAllProductsByCategoryDemo(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryIdDemo(categoryId);

        return products
                .stream()
                .map(productMapper::productToProductResponse)
                .toList();

    }
    public Page<ProductResponse> getProductsForUser(
            Integer categoryId, String keyword, String price,
            String tagSlug, String sort, Pageable pageable
    ) {
        String cleanKeyword = productPageService.cleanString(keyword);
        String cleanPrice = productPageService.cleanString(price);
        String cleanTag = productPageService.cleanString(tagSlug);
        String currentSort = (sort != null) ? sort : "default";

        Page<Product> productPage = switch (currentSort) {
            case "price_asc" -> productRepository.searchProductsPriceAsc(cleanKeyword, categoryId, cleanPrice, cleanTag, pageable);
            case "price_desc" -> productRepository.searchProductsPriceDesc(cleanKeyword, categoryId, cleanPrice, cleanTag, pageable);
            case "name_asc" -> productRepository.searchProductsNameAsc(cleanKeyword, categoryId, cleanPrice, cleanTag, pageable);
            default -> productRepository.searchProductsNewest(cleanKeyword, categoryId, cleanPrice, cleanTag, pageable);
        };

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

