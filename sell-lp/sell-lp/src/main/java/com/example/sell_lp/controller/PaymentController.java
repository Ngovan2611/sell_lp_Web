package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.request.PaymentRequest;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.order.OrderService;
import com.example.sell_lp.service.PaymentService;
import com.example.sell_lp.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    AuthenticationService authenticationService;
    OrderService orderService;
    VNPayService vnPayService;
    PaymentService paymentService;

    @PostMapping("/order/create")
    public String createOrder(@ModelAttribute OrderCreationRequest request,
                              @RequestParam(value = "paymentMethod", defaultValue = "COD") String paymentMethod,
                              @CookieValue(value = "jwt", required = false) String token,
                              HttpServletRequest httpRequest) throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) return "redirect:/login";

        Order savedOrder = orderService.save(request);

        if ("VNPAY".equals(paymentMethod)) {
            String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();

            int amount = savedOrder.getTotalAmount().intValue();
            String orderInfo = "Thanh toan don hang #" + savedOrder.getOrderId();

            String vnpayUrl = vnPayService.createOrder(amount, orderInfo, baseUrl + "/vnpay-return");

            return "redirect:" + vnpayUrl;
        }

        return "redirect:/history-order";
    }
    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String amountStr = request.getParameter("vnp_Amount");
        String bankCode = request.getParameter("vnp_BankCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");

        Integer orderId = extractOrderId(orderInfo);

        if (orderId != null) {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(orderId);
            paymentRequest.setMethod("VNPAY - " + bankCode);
            paymentRequest.setTransactionId(transactionNo);
            paymentRequest.setResponseCode(vnp_ResponseCode);
            paymentRequest.setAmount(new BigDecimal(amountStr).divide(new BigDecimal(100)));

            if ("00".equals(vnp_ResponseCode)) {
                paymentRequest.setStatus(PaymentStatus.SUCCESS.name());
                paymentService.createPayment(paymentRequest);
                orderService.updateOrderStatus(orderId, OrderStatus.PENDING.name());

                model.addAttribute("orderId", orderId);
                model.addAttribute("totalPrice", paymentRequest.getAmount());
                model.addAttribute("bankCode", bankCode);
                model.addAttribute("message", "Giao dịch đã được xác nhận thành công.");
                return "vnpay-success";
            } else {
                paymentRequest.setStatus(PaymentStatus.FAILED.name());
                paymentService.createPayment(paymentRequest);

                orderService.updateOrderStatus(orderId, OrderStatus.FAILURE.name());

                String errorMsg = "payment_failed";
                if ("24".equals(vnp_ResponseCode)) errorMsg = "payment_cancelled";

                return "redirect:/history-order?error=" + errorMsg;
            }
        }
        return "redirect:/";
    }

    private Integer extractOrderId(String orderInfo) {
        try {
            if (orderInfo != null && orderInfo.contains("#")) {
                String idStr = orderInfo.substring(orderInfo.indexOf("#") + 1).trim();
                return Integer.parseInt(idStr);
            }
        } catch (Exception e) {
            System.err.println("Lỗi trích xuất ID: " + e.getMessage());
        }
        return null;
    }
}


