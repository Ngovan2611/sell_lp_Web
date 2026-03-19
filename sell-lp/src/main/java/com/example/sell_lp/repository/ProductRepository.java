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
}
