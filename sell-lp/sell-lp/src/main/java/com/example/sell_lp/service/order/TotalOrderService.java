package com.example.sell_lp.service.order;


import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TotalOrderService {
    @Autowired
    private OrderRepository orderRepository;


    public Double getTotalOrder() {
        return orderRepository.getTotalRevenue();
    }

    public List<Double> getRevenueLast7Days() {
        return orderRepository.getRevenueLast7Days();
    }

    public List<String> getDaysLast7Days() {
        return orderRepository.getDaysLast7Days();
    }

    public Long countByStatusPendingAndPreparing() {
        return orderRepository.countByStatus(OrderStatus.PENDING.name()) + orderRepository.countByStatus(OrderStatus.PREPARING.name());
    }
    public Long countByStatusSuccess() {
        return orderRepository.countByStatus(OrderStatus.SUCCESS.name());
    }
    public Long countByStatusPending() {
        return orderRepository.countByStatus(OrderStatus.PENDING.name());
    }
    public Long countByStatusFailed() {
        return orderRepository.countByStatus(OrderStatus.FAILURE.name());
    }
}
