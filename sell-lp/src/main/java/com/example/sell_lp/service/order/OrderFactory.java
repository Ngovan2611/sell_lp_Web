package com.example.sell_lp.service.order;

import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.entity.Address;

import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;


@Component
public class OrderFactory {

    public Order createOrder(User user, Address address) {
        Order order = new Order();
        order.setUser(user);
        order.setRecipientName(address.getRecipientName());
        order.setPhone(address.getPhone());
        order.setFullAddress(address.getFullAddress());
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());
        return order;
    }

    public void attachItems(Order order, List<CartItem> cartItems) {
        List<OrderItem> items = cartItems.stream().map(c -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(c.getVariant());
            oi.setQuantity(c.getQuantity());
            oi.setUnitPrice(c.getUnitPrice());
            return oi;
        }).toList();

        order.setOrderItems(items);
    }
}