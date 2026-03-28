package com.example.sell_lp.dto.response;



import com.example.sell_lp.entity.ProductVariant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CartItemResponse {
    Integer cartItemId;
    Integer quantity;
    ProductVariant variant;
    Double unitPrice;
    public Double getUnitPrice() {
        return variant != null ? variant.getPrice() : 0.0;
    }
}
