package com.example.sell_lp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariant {

    @Id
    Long variantId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name = "color_id")
    Color color;

    @ManyToOne
    @JoinColumn(name = "ram_id")
    Ram ram;

    @ManyToOne
    @JoinColumn(name = "rom_id")
    Rom rom;

    Integer stockQty;

    double price;
}
