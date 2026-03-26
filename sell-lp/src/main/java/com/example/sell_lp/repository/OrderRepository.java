package com.example.sell_lp.repository;


import com.example.sell_lp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_UserId(String userId);

    Order findByOrderId(Integer orderId);

    List<Order> findByStatusAndCreatedAtBefore(String status, Date createdAt);

}
