package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.entity.Address;

import com.example.sell_lp.enums.Statuses;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.repository.AddressRepository;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.OrderRepository;
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

    public void save(OrderCreationRequest req) {

        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        Order order = new Order();

        // user
        order.setUser(user);

        // snapshot address
        order.setRecipientName(address.getRecipientName());
        order.setPhone(address.getPhone());
        order.setFullAddress(address.getFullAddress());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setWard(address.getWard());
        order.setStreet(address.getStreet());
        order.setLat(address.getLat());
        order.setLng(address.getLng());

        order.setStatus(Statuses.PENDING.name());
        order.setCreatedAt(new Date());
        order.setOrderDate(new Date());

        List<Long> itemIds = Arrays.stream(req.getIds().split(","))
                .map(Long::parseLong)
                .toList();

        List<CartItem> items = cartItemRepository.findAllById(itemIds);

        BigDecimal total = items.stream()
                .map(i -> BigDecimal.valueOf(i.getUnitPrice()) // convert từ Double
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = new BigDecimal("30000");
        total = total.add(shipping);

        order.setTotalAmount(total);

        orderRepository.save(order);
        cartItemRepository.deleteAll(items);
    }
}