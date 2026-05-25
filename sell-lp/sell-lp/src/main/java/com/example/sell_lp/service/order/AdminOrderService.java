package com.example.sell_lp.service.order;

import com.example.sell_lp.dto.response.order.OrderItemResponse;
import com.example.sell_lp.dto.response.order.OrderResponse;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.Payment;
import com.example.sell_lp.enums.NotificationType;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.enums.PaymentMethod;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.mapper.OrderMapper;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.order.OrderRepository;
import com.example.sell_lp.service.notification.NotificationService;
import com.example.sell_lp.service.payment.PaymentService;
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
    NotificationService notificationService;
    private final PaymentService paymentService;

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
        notificationService.sendToUser(NotificationType.ORDER_CANCELLED_SYSTEM,
                "đơn hàng: #" + String.valueOf(order.getOrderId()),
                order.getUser().getUserId());
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
        Payment payment = paymentService.getByOrderOrderId(orderId);

        if(payment.getMethod().equals(PaymentMethod.VN_PAY.name()) && payment.getStatus().equals(PaymentStatus.PENDING.name())) {
            throw new RuntimeException("không thể cập nhật trạng thái vì đơn hàng chưa được thanh toán");
        }
        if (nextStatus.equals(OrderStatus.FAILURE.name())) {
            stockOrder.rollbackStock(order);

        }

        order.setStatus(nextStatus);
        orderRepository.save(order);
        sendNotificationToUser(order, nextStatus);
    }
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

    private void sendNotificationToUser(Order order, String status) {


        if(status.equals(OrderStatus.SUCCESS.name())) {
            Payment payment = paymentService.getByOrderOrderId(order.getOrderId());
            if(payment.getMethod().equals(PaymentMethod.COD.name())) {
                paymentService.completeCODPayment(order.getOrderId());

            }
            notificationService.sendToUser(NotificationType.ORDER_DELIVERED,
                    "đơn hàng: #" + String.valueOf(order.getOrderId()),
                    order.getUser().getUserId());
        }
        if(status.equals(OrderStatus.PREPARING.name())) {
            notificationService.sendToUser(NotificationType.ORDER_CONFIRMED,
                    "đơn hàng: #" + String.valueOf(order.getOrderId()),
                    order.getUser().getUserId());
        }

        if(status.equals(OrderStatus.SHIPPING.name())) {
            notificationService.sendToUser(NotificationType.ORDER_SHIPPING,
                    "đơn hàng: #" + String.valueOf(order.getOrderId()),
                    order.getUser().getUserId());
        }
    }
}
