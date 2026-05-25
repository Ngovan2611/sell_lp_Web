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

    public List<ProductResponse> getAllProductsByCategoryDemo(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryIdDemo(categoryId);

        return products
                .stream()
                .map(productMapper::productToProductResponse)
                .toList();

    }
    public Page<ProductResponse> getProductsForUser(
            Integer categoryId,
            String keyword,
            String price,
            String sort,
            Pageable pageable
    ) {

        Page<Product> productPage;

        switch (sort) {

            case "price_asc":

                productPage =
                        productRepository
                                .searchProductsPriceAsc(
                                        keyword,
                                        categoryId,
                                        price,
                                        pageable
                                );

                break;

            case "price_desc":

                productPage =
                        productRepository
                                .searchProductsPriceDesc(
                                        keyword,
                                        categoryId,
                                        price,
                                        pageable
                                );

                break;

            case "name_asc":

                productPage =
                        productRepository
                                .searchProductsNameAsc(
                                        keyword,
                                        categoryId,
                                        price,
                                        pageable
                                );

                break;

            default:

                productPage =
                        productRepository
                                .searchProductsNewest(
                                        keyword,
                                        categoryId,
                                        price,
                                        pageable
                                );
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

