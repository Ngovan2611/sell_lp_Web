function updateStatus(id, status) {
    let message = "Xác nhận chuyển trạng thái đơn hàng?";
    if(status === 'PREPARING') message = "Xác nhận duyệt và bắt đầu đóng gói đơn hàng?";
    if(status === 'SHIPPING') message = "Xác nhận đã bàn giao hàng cho đơn vị vận chuyển?";
    if(status === 'SUCCESS') message = "Xác nhận đơn hàng đã giao tới khách và thu tiền thành công?";

    if(!confirm(message)) return;

    fetch(`/admin/orders/update-status/${id}?status=${status}`, { method: 'POST' })
        .then(res => {
            if(res.ok) {
                alert("Cập nhật trạng thái thành công!");
                location.reload();
            } else {
                alert("Lỗi: Không thể cập nhật trạng thái.");
            }
        });
}

function adminCancel(id) {
    if(!confirm("Cảnh báo: Hủy đơn sẽ tự động cộng lại số lượng sản phẩm vào kho. Bạn chắc chắn chứ?")) return;

    fetch(`/admin/orders/cancel/${id}`, { method: 'POST' })
        .then(res => {
            if(res.ok) {
                alert("Đã hủy đơn và hoàn trả kho hàng.");
                location.href = "/admin/orders";
            } else {
                alert("Không thể hủy đơn hàng này.");
            }
        });
}