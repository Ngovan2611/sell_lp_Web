package com.example.sell_lp.repository.order;


import com.example.sell_lp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_UserId(String userId);

    Order findByOrderId(Integer orderId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'SUCCESS'")
    Double getTotalRevenue();

    long countByStatus(String status);


    @Query(value = "SELECT SUM(total_amount) FROM `order` " +
            "WHERE status = 'SUCCESS' AND order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(order_date) ORDER BY DATE(order_date) ASC", nativeQuery = true)
    List<Double> getRevenueLast7Days();

    @Query(value = "SELECT DATE_FORMAT(order_date, '%d/%m') " +
            "FROM `order` " +
            "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE_FORMAT(order_date, '%d/%m') " + // Khớp hoàn toàn với SELECT
            "ORDER BY MIN(order_date) ASC", nativeQuery = true)
    List<String> getDaysLast7Days();
    @Query("SELECT o FROM Order o WHERE " +
            "(:orderId IS NULL OR o.orderId = :orderId) AND " +
            "(:customerName IS NULL OR LOWER(o.recipientName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
            "(:phone IS NULL OR o.phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:status IS NULL OR o.status = :status)")
    Page<Order> searchOrdersAdmin(
            @Param("orderId") Long orderId,
            @Param("customerName") String customerName,
            @Param("phone") String phone,
            @Param("status") String status,
            Pageable pageable);
}

