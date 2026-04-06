package com.example.sell_lp.service.order;

import com.example.sell_lp.dto.response.OrderItemResponse;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderService {
    StockOrder stockOrder;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    PaymentMapper paymentMapper;

    public void cancelOrderByAdmin(Integer id) {

        Order order = orderRepository.findByOrderId(id);

        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng không thể hủy");
        }

        order.setStatus(OrderStatus.FAILURE.name());
        stockOrder.rollbackStock(order);
        orderRepository.save(order);
    }
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(order -> {
            OrderResponse res = orderMapper.toOrderResponse(order);

            res.setItems(order.getOrderItems().stream()
                    .map(orderMapper::toOrderItemResponse).toList());

            if (order.getPayments() != null) {
                res.setPayments(order.getPayments().stream()
                        .map(paymentMapper::toResponse).toList());
            }
            return res;
        });
    }
    public void updateOrderStatus(Integer orderId, String nextStatus) {
        Order order = orderRepository.findByOrderId(orderId);

        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        String currentStatus = order.getStatus();

        if (currentStatus.equals(OrderStatus.SUCCESS.name()) ||
                currentStatus.equals(OrderStatus.FAILURE.name())) {
            throw new RuntimeException("Đơn hàng đã hoàn tất hoặc đã hủy, không thể thay đổi trạng thái.");
        }

        if (nextStatus.equals(OrderStatus.FAILURE.name())) {
            stockOrder.rollbackStock(order);
        }

        order.setStatus(nextStatus);
        orderRepository.save(order);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse getOrderDetailForAdmin(Integer orderId) {

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
}
