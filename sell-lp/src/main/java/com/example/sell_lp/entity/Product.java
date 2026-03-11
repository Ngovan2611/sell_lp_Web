package com.example.sell_lp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Product {
    @Id
    int productId;
    String name;
    String description;
    double price;
    int stock;
    boolean isActive;
    @OneToMany(mappedBy = "product")
    List<ProductVariant> variants;
}
