package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.request.OrderItemCreationRequest;
import com.example.sell_lp.dto.response.OrderItemResponse;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.entity.Address;

import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.mapper.OrderItemMapper;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.repository.AddressRepository;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.OrderItemRepository;
import com.example.sell_lp.repository.OrderRepository;
import com.example.sell_lp.repository.ProductVariantRepository;
import com.example.sell_lp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    public void save(OrderCreationRequest req) {

        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        Order order = new Order();

        order.setUser(user);

        order.setRecipientName(address.getRecipientName());
        order.setPhone(address.getPhone());
        order.setFullAddress(address.getFullAddress());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setWard(address.getWard());
        order.setStreet(address.getStreet());
        order.setLat(address.getLat());
        order.setLng(address.getLng());

        order.setStatus(OrderStatus.PENDING.name());
        order.setCreatedAt(new Date());
        order.setOrderDate(new Date());

        List<Long> itemIds = Arrays.stream(req.getIds().split(","))
                .map(Long::parseLong)
                .toList();

        List<CartItem> items = cartItemRepository.findAllById(itemIds);

        BigDecimal total = items.stream()
                .map(i -> BigDecimal.valueOf(i.getUnitPrice())
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = new BigDecimal("30000");
        total = total.add(shipping);
        order.setTotalAmount(total);
        orderRepository.save(order);
        List<OrderItem> orderItems = items.stream()
                .map(cart -> {
                    var variant = cart.getVariant();

                    if (variant.getStockQty() < cart.getQuantity()) {
                        throw new RuntimeException(
                                "Sản phẩm " + variant.getProduct().getName() + " không đủ hàng"
                        );
                    }

                    variant.setStockQty(variant.getStockQty() - cart.getQuantity());
                    productVariantRepository.save(variant);
                    OrderItemCreationRequest reqItem = new OrderItemCreationRequest(
                            cart.getQuantity(),
                            cart.getUnitPrice(),
                            cart.getVariant().getProduct().getName(),
                            cart.getVariant().getProduct().getName(),
                            cart.getVariant().getProduct().getImages().get(0).getUrl(),
                            order.getOrderId(),
                            cart.getVariant().getVariantId()
                    );

                    OrderItem oi = orderItemMapper.toEntity(reqItem);

                    oi.setOrder(order);
                    return oi;
                })
                .toList();
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(items);
    }

    public List<OrderResponse> getOrdersByUsername(String username) {

        User user = userRepository.findByUsername(username);

        List<Order> orders = orderRepository.findByUser_UserId(user.getUserId());

        return orders.stream().map(order -> {
            OrderResponse res = orderMapper.toOrderResponse(order);

            List<OrderItemResponse> items = order.getOrderItems()
                    .stream()
                    .map(orderMapper::toOrderItemResponse)
                    .toList();

            res.setItems(items);

            return res;
        }).toList();
    }
}