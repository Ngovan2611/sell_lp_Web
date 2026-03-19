package com.example.sell_lp.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Product {
    @Id
    int productId;
    String name;
    String description;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "product")
    List<ProductVariant> variants;

    @ManyToOne
    @JoinColumn(name = "category_id")   // FK trong bảng product
    Category category;

    @OneToMany(mappedBy = "product")
    List<ProductImage> images;


}
