package com.example.sell_lp.enums;

import lombok.Getter;

@Getter
public enum NotificationType {

    REGISTER_SUCCESS("Đăng ký thành công"),
    ORDER_SUCCESS("Đơn hàng thành công"),
    PROMOTION("Khuyến mãi"),
    SYSTEM_ALERT("Cảnh báo hệ thống"),
    FAVORITE("Yêu thích"),

    // Thêm các trạng thái đơn hàng mới tại đây
    ORDER_CANCELLED_SYSTEM("Đơn hàng bị hủy bởi hệ thống"),
    ORDER_CANCELLED_USER("Bạn đã hủy đơn hàng"),
    ORDER_CONFIRMED("Đơn hàng đã xác nhận"),
    ORDER_SHIPPING("Đơn hàng đang vận chuyển"),
    ORDER_DELIVERED("Đơn hàng đã giao thành công"),
    OTHER("Thông báo từ quản trị viên");
    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

}