package com.example.sell_lp.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thêm dòng này
    int productId;
    String name;
    String description;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "product")
    List<ProductVariant> variants;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    // Tại Product.java
    @ManyToMany(fetch = FetchType.EAGER) // Thêm EAGER như bước trước để xử lý LAZY loading khi gọi API
    @JoinTable(
            name = "product_tags",                             // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "product_id"),    // Khóa ngoại liên kết tới bảng Product
            inverseJoinColumns = @JoinColumn(name = "tag_id")  // Khóa ngoại liên kết tới bảng Tag
    )
    private Set<Tag> tags;

}
