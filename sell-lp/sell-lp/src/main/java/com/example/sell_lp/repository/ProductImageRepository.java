package com.example.sell_lp.repository;

import com.example.sell_lp.entity.Product;
import com.example.sell_lp.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteByProduct(Product product);
}
