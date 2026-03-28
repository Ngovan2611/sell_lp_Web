package com.example.sell_lp.repository;


import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
SELECT p FROM Product p
LEFT JOIN FETCH p.images
WHERE p.category.categoryId = :categoryId
""")
    List<Product> findByCategoryCategoryIdDemo(Integer categoryId);
    @Query("""
SELECT p FROM Product p
LEFT JOIN FETCH p.images
WHERE p.category.categoryId = :categoryId
""")
    Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    Product findByProductId(Long productId);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.productId <> :id")
    List<Product> findRelatedProducts(@Param("category") Category category, @Param("id") Long id, Pageable pageable);
    @Query("SELECT p FROM Product p LEFT JOIN p.variants v GROUP BY p.productId ORDER BY MIN(v.price) ASC")
    Page<Product> findAllOrderByMinPriceAsc(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.variants v GROUP BY p.productId ORDER BY MIN(v.price) DESC")
    Page<Product> findAllOrderByMinPriceDesc(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.variants v WHERE p.category.categoryId = :categoryId GROUP BY p.productId ORDER BY MIN(v.price) ASC")
    Page<Product> findAllByCategoryIdOrderByMinPriceAsc(@Param("categoryId") Integer categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.variants v WHERE p.category.categoryId = :categoryId GROUP BY p.productId ORDER BY MIN(v.price) DESC")
    Page<Product> findAllByCategoryIdOrderByMinPriceDesc(@Param("categoryId") Integer categoryId, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.categoryId = :categoryId)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Integer categoryId,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.variants v WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
            "GROUP BY p.productId ORDER BY MIN(v.price) ASC")
    Page<Product> searchProductsOrderByPriceAsc(@Param("keyword") String keyword,
                                                @Param("categoryId") Integer categoryId,
                                                Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.variants v WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
            "GROUP BY p.productId ORDER BY MIN(v.price) DESC")
    Page<Product> searchProductsOrderByPriceDesc(@Param("keyword") String keyword,
                                                 @Param("categoryId") Integer categoryId,
                                                 Pageable pageable);
}
