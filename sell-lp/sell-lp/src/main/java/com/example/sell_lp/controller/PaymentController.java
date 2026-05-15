package com.example.sell_lp.controller;

import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.request.PaymentRequest;
import com.example.sell_lp.entity.Payment; // SỬA: Đổi import từ Order sang Payment
import com.example.sell_lp.enums.PaymentMethod;
import com.example.sell_lp.service.payment.PaymentService;
import com.example.sell_lp.service.payment.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("isAuthenticated()")
public class PaymentController {

    PaymentService paymentService;
    VNPayService vnPayService;

    @PostMapping("/order/create")
    public String createOrder(@ModelAttribute OrderCreationRequest request,
                              @RequestParam(value = "paymentMethod", defaultValue = "COD") String paymentMethod,
                              Principal principal,
                              HttpServletRequest httpRequest) throws Exception {

        if (principal == null) return "redirect:/login";

        // SỬA 1: Hứng giá trị trả về là đối tượng Payment (đã được gán mã LAP... ngầm từ Service)
        Payment savedPayment = paymentService.createOrderAndInitPayment(request, paymentMethod);

        if (PaymentMethod.VN_PAY.name().equalsIgnoreCase(paymentMethod)) {
            String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();

            // SỬA 2: Lấy thông tin giá tiền và Order ID thông qua đối tượng savedPayment vừa hứng
            int amount = savedPayment.getAmount().intValue();
            String orderInfo = "Don hang #" + savedPayment.getOrder().getOrderId();

            // SỬA 3: Truyền mã tự sinh (savedPayment.getTransactionId()) sang VNPayService làm vnp_TxnRef
            String vnpayUrl = vnPayService.createOrder(amount, orderInfo, baseUrl + "/vnpay-return", savedPayment.getTransactionId());

            // Điều hướng sang cổng VNPay
            return "redirect:" + vnpayUrl;
        }

        // Nếu chọn COD, điều hướng thẳng về trang lịch sử đơn hàng
        return "redirect:/history-order";
    }

    @GetMapping("/vnpay-return")
    public String paySuccessfully(HttpServletRequest request, Model model) {
        // Thu thập các tham số từ VNPay gửi về vào một Map
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> params.put(key, value[0]));

        try {
            // Đẩy Map tham số xuống Service xử lý cập nhật trạng thái dữ liệu ngầm (đối soát qua mã LAP...)
            PaymentRequest result = paymentService.processVNPayReturn(params);

            // Logic điều hướng dựa trên kết quả trả về từ Service (Giữ nguyên)
            if ("00".equals(result.getResponseCode())) {
                model.addAttribute("orderId", result.getOrderId());
                model.addAttribute("totalPrice", result.getAmount());
                model.addAttribute("bankCode", request.getParameter("vnp_BankCode"));
                model.addAttribute("message", "Giao dịch đã được xác nhận thành công.");

                // Trả về giao diện thông báo thành công
                return "vnpay-success";
            } else {
                String errorMsg = "payment_failed";
                if ("24".equals(result.getResponseCode())) {
                    errorMsg = "payment_cancelled";
                }
                // Điều hướng về trang lịch sử kèm mã lỗi query string
                return "redirect:/history-order?error=" + errorMsg;
            }
        } catch (Exception e) {
            // Nếu có lỗi hệ thống hoặc lỗi trích xuất dữ liệu, quay về trang lịch sử đơn hàng
            return "redirect:/history-order?error=system_error";
        }
    }
    @PostMapping("/order/repay")
    public String rePayOrder(@RequestParam("orderId") Integer orderId,
                             HttpServletRequest httpRequest) throws Exception {

        // 1. Tìm thông tin thanh toán cũ trong DB thông qua orderId
        Payment payment = paymentService.getByOrderOrderId(orderId);
        if (payment == null) {
            return "redirect:/history-order?error=payment_not_found";
        }

        // 2. Chuẩn bị các tham số cấu hình gửi sang cổng VNPay giống như lúc tạo mới
        String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();
        int amount = payment.getAmount().intValue();
        String orderInfo = "Don hang #" + orderId;

        // 3. Sử dụng lại chính mã giao dịch (LAP...) cũ đã lưu trong DB làm vnp_TxnRef
        // Việc này giúp VNPay hiểu là bạn đang thực hiện thanh toán lại cho phiên giao dịch này
        String vnpayUrl = vnPayService.createOrder(amount, orderInfo, baseUrl + "/vnpay-return", payment.getTransactionId());

        // 4. Redirect người dùng quay trở lại trang nhập thẻ/quét mã QR của VNPay
        return "redirect:" + vnpayUrl;
    }
}