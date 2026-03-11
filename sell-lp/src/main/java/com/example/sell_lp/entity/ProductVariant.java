package com.example.sell_lp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariant {

    @Id
    int variantId;

    double price;
    int stockQty;

    @ManyToOne
    Product product;

    @ManyToOne
    Ram ram;

    @ManyToOne
    Rom rom;

    @ManyToOne
    Color color;
}
