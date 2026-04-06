package com.example.sell_lp.service.order;


import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.mapper.OrderItemMapper;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.AddressRepository;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.OrderItemRepository;
import com.example.sell_lp.repository.OrderRepository;
import com.example.sell_lp.repository.ProductVariantRepository;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
@Transactional
public class StockOrder {

    ProductVariantRepository productVariantRepository;

    public void rollbackStock(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                productVariantRepository.findByIdWithLock(item.getVariant().getVariantId())
                        .ifPresent(variant -> {
                            variant.setStockQty(variant.getStockQty() + item.getQuantity());
                            productVariantRepository.save(variant);
                            log.info("Đã hoàn kho sản phẩm: {} số lượng: {}", variant.getVariantId(), item.getQuantity());
                        });
            }
        }
    }
    public void processVariantStock(ProductVariant variant, int qty) {
        ProductVariant lockedVariant = productVariantRepository.findByIdWithLock(variant.getVariantId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không còn tồn tại hoặc đang bị khóa"));

        if (lockedVariant.getStockQty() < qty) {
            throw new RuntimeException("Sản phẩm " + lockedVariant.getProduct().getName() + " không đủ hàng");
        }

        lockedVariant.setStockQty(lockedVariant.getStockQty() - qty);
        productVariantRepository.save(lockedVariant);
    }
}
