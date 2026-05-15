package com.example.sell_lp.repository.product;


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


    // tim kiem san pham demo trong trang chu
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
    Page<Product> findAll(Pageable pageable);

    Product findByProductId(Long productId);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.productId <> :id")
    List<Product> findRelatedProducts(@Param("category") Category category, @Param("id") Long id, Pageable pageable);

    // tìm kiem theo gia giam dan
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v
WHERE

p.isActive = true

AND
(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :price IS NULL
    OR :price = ''

    OR (:price = '1'
        AND v.price < 10000000)

    OR (:price = '2'
        AND v.price BETWEEN 10000000 AND 20000000)

    OR (:price = '3'
        AND v.price > 20000000)
)

GROUP BY p.productId

ORDER BY MIN(v.price) DESC
""")
    Page<Product> searchProductsPriceDesc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("price") String price,
            Pageable pageable
    );
    // tim kiem theo gia tang dan

    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v
WHERE

p.isActive = true

AND
(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :price IS NULL
    OR :price = ''

    OR (:price = '1'
        AND v.price < 10000000)

    OR (:price = '2'
        AND v.price BETWEEN 10000000 AND 20000000)

    OR (:price = '3'
        AND v.price > 20000000)
)

GROUP BY p.productId

ORDER BY MIN(v.price) ASC
""")
    Page<Product> searchProductsPriceAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("price") String price,
            Pageable pageable
    );
    // lấy sản phẩm mơới nhat
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v
WHERE
p.isActive = true

AND
(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :price IS NULL
    OR :price = ''

    OR (:price = '1'
        AND v.price < 10000000)

    OR (:price = '2'
        AND v.price BETWEEN 10000000 AND 20000000)

    OR (:price = '3'
        AND v.price > 20000000)
)

GROUP BY p.productId

ORDER BY p.productId DESC
""")
    Page<Product> searchProductsNewest(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("price") String price,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v
WHERE

p.isActive = true

AND
(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :price IS NULL
    OR :price = ''

    OR (:price = '1'
        AND v.price < 10000000)

    OR (:price = '2'
        AND v.price BETWEEN 10000000 AND 20000000)

    OR (:price = '3'
        AND v.price > 20000000)
)

GROUP BY p.productId

ORDER BY p.name ASC
""")
    Page<Product> searchProductsNameAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("price") String price,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v

WHERE

(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :active IS NULL
    OR p.isActive = :active
)

GROUP BY p.productId

HAVING

(
    :stock IS NULL
    OR :stock = ''

    OR (
        :stock = 'in'
        AND SUM(v.stockQty) > 10
    )

    OR (
        :stock = 'low'
        AND SUM(v.stockQty)
            BETWEEN 1 AND 10
    )

    OR (
        :stock = 'out'
        AND SUM(v.stockQty) = 0
    )
)

ORDER BY p.productId DESC
""")
    Page<Product> adminSearchNewest(
            String keyword,
            Integer categoryId,
            String stock,
            Boolean active,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v

WHERE

(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :active IS NULL
    OR p.isActive = :active
)

GROUP BY p.productId

HAVING

(
    :stock IS NULL
    OR :stock = ''

    OR (
        :stock = 'in'
        AND SUM(v.stockQty) > 10
    )

    OR (
        :stock = 'low'
        AND SUM(v.stockQty)
            BETWEEN 1 AND 10
    )

    OR (
        :stock = 'out'
        AND SUM(v.stockQty) = 0
    )
)

ORDER BY MIN(v.price) ASC
""")
    Page<Product> adminSearchPriceAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("stock") String stock,
            @Param("active") Boolean active,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v

WHERE

(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :active IS NULL
    OR p.isActive = :active
)

GROUP BY p.productId

HAVING

(
    :stock IS NULL
    OR :stock = ''

    OR (
        :stock = 'in'
        AND SUM(v.stockQty) > 10
    )

    OR (
        :stock = 'low'
        AND SUM(v.stockQty)
            BETWEEN 1 AND 10
    )

    OR (
        :stock = 'out'
        AND SUM(v.stockQty) = 0
    )
)

ORDER BY MIN(v.price) DESC
""")
    Page<Product> adminSearchPriceDesc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("stock") String stock,
            @Param("active") Boolean active,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v

WHERE

(
    :categoryId IS NULL
    OR p.category.categoryId = :categoryId
)

AND
(
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND
(
    :active IS NULL
    OR p.isActive = :active
)

GROUP BY p.productId

HAVING

(
    :stock IS NULL
    OR :stock = ''

    OR (
        :stock = 'in'
        AND SUM(v.stockQty) > 10
    )

    OR (
        :stock = 'low'
        AND SUM(v.stockQty)
            BETWEEN 1 AND 10
    )

    OR (
        :stock = 'out'
        AND SUM(v.stockQty) = 0
    )
)

ORDER BY p.name ASC
""")
    Page<Product> adminSearchNameAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("stock") String stock,
            @Param("active") Boolean active,
            Pageable pageable
    );
    @Query("""
SELECT p
FROM Product p
LEFT JOIN p.variants v
GROUP BY p
HAVING COALESCE(SUM(v.stockQty),0) BETWEEN 1 AND 10
""")
    List<Product> findLowStockProducts();
}

