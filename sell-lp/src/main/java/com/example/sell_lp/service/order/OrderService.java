package com.example.sell_lp.service.order;

import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.request.OrderItemCreationRequest;
import com.example.sell_lp.dto.response.OrderItemResponse;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.entity.Address;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.mapper.OrderItemMapper;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.AddressRepository;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.OrderItemRepository;
import com.example.sell_lp.repository.OrderRepository;
import com.example.sell_lp.repository.ProductVariantRepository;
import com.example.sell_lp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class OrderService {
    OrderRepository orderRepository;
    UserRepository userRepository;
    AddressRepository addressRepository;
    CartItemRepository cartItemRepository;
    OrderMapper orderMapper;
    OrderItemMapper orderItemMapper;
    OrderItemRepository orderItemRepository;
    ProductVariantRepository productVariantRepository;
    PaymentMapper paymentMapper;

    @Transactional
    public Order save(OrderCreationRequest req) {
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

        String[] idStrings = req.getIds().split(",");
        List<OrderItem> orderItemsList = new java.util.ArrayList<>();
        BigDecimal totalProducts = BigDecimal.ZERO;

        for (String idStr : idStrings) {
            Long id = Long.parseLong(idStr);

            Optional<CartItem> cartItemOpt = cartItemRepository.findById(id);

            if (cartItemOpt.isPresent()) {
                CartItem cart = cartItemOpt.get();
                processVariantStock(cart.getVariant(), cart.getQuantity());

                OrderItem oi = createOrderItemEntity(order, cart.getVariant(), cart.getQuantity(), cart.getUnitPrice());
                orderItemsList.add(oi);
                totalProducts = totalProducts.add(BigDecimal.valueOf(cart.getUnitPrice()).multiply(BigDecimal.valueOf(cart.getQuantity())));

                cartItemRepository.delete(cart);
            } else {
                var variant = productVariantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không hợp lệ hoặc đã hết hạn giỏ hàng"));

                int quantity = 1;
                processVariantStock(variant, quantity);

                OrderItem oi = createOrderItemEntity(order, variant, quantity, variant.getPrice());
                orderItemsList.add(oi);
                totalProducts = totalProducts.add(BigDecimal.valueOf(variant.getPrice()).multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BigDecimal shipping = new BigDecimal("30000");
        order.setTotalAmount(totalProducts.add(shipping));

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItemsList);
        return order;
    }

    private void processVariantStock(ProductVariant variant, int qty) {
        if (variant.getStockQty() < qty) {
            throw new RuntimeException("Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
        }
        variant.setStockQty(variant.getStockQty() - qty);
        productVariantRepository.save(variant);
    }

    private OrderItem createOrderItemEntity(Order order, com.example.sell_lp.entity.ProductVariant variant, int qty, Double price) {
        OrderItemCreationRequest reqItem = new OrderItemCreationRequest(
                qty,
                price,
                variant.getProduct().getName(),
                variant.getProduct().getName(),
                variant.getProduct().getImages().isEmpty() ? "" : variant.getProduct().getImages().getFirst().getUrl(),
                order.getOrderId(),
                variant.getVariantId()
        );
        OrderItem oi = orderItemMapper.toEntity(reqItem);
        oi.setOrder(order);
        return oi;
    }
    public List<OrderResponse> getOrdersByUsername(String username) {

        User user = userRepository.findByUsername(username);
        List<Order> orders = orderRepository.findByUser_UserId(user.getUserId());

        return orders.stream().map(order -> {
            OrderResponse res = orderMapper.toOrderResponse(order);

            res.setItems(order.getOrderItems().stream()
                    .map(orderMapper::toOrderItemResponse).toList());

            if (order.getPayments() != null) {
                res.setPayments(order.getPayments().stream()
                        .map(paymentMapper::toResponse).toList());
            }

            return res;
        }).toList();
    }
    public OrderResponse getOrderDetail(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        OrderResponse res = orderMapper.toOrderResponse(order);

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(orderMapper::toOrderItemResponse)
                .toList();
        if (order.getPayments() != null) {
            res.setPayments(order.getPayments().stream()
                    .map(paymentMapper::toResponse).toList());
        }

        res.setItems(items);

        return res;
    }
    public void updateOrderStatus(Integer orderId, String request) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }
        if (!order.getStatus().equals(OrderStatus.PENDING.name())) {
            throw new RuntimeException("Không thể cập nhật trạng thái");
        }
        order.setStatus(request);
        orderRepository.save(order);
    }
    public void cancelOrder(Integer id) {

        Order order = orderRepository.findByOrderId(id);

        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng không thể hủy");
        }

        order.setStatus(OrderStatus.FAILURE.name());
        rollbackStock(order);
        orderRepository.save(order);
    }
    private void rollbackStock(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                ProductVariant variant = productVariantRepository.findById(item.getVariant().getVariantId())
                        .orElse(null);
                if (variant != null) {
                    variant.setStockQty(variant.getStockQty() + item.getQuantity());
                    productVariantRepository.save(variant);
                    System.out.println("Đã hoàn kho sản phẩm: " + variant.getVariantId() + " số lượng: " + item.getQuantity());
                }
            }
        }
    }
}