package com.example.sell_lp.dto.response;



import com.example.sell_lp.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CartItemResponse {
    private Integer cartItemId;
    private Integer quantity;
    private ProductVariant variant;
    private Double unitPrice;
    public Double getUnitPrice() {
        return variant != null ? variant.getPrice() : 0.0;
    }
}
