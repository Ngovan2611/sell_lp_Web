document.querySelectorAll('.order-card').forEach(card => {
    card.addEventListener('click', function (e) {
        if (e.target.closest('.btn-group') || e.target.closest('.btn')) {
            return;
        }
        const orderId = this.dataset.id;
        if (orderId) {
            location.href = `/admin/orders/detail/${orderId}`;
        }
    });
});

function updateStatus(orderId, status) {
    if(!confirm("Xác nhận thay đổi trạng thái đơn hàng?")) return;
    fetch(`/admin/orders/update-status/${orderId}?status=${status}`, { method: 'POST' })
        .then(res => {
            if(res.ok) { location.reload(); }
            else { alert("Lỗi khi cập nhật"); }
        });
}

function confirmCancel(orderId) {
    if(!confirm("Bạn có chắc chắn muốn HỦY đơn hàng này? Hệ thống sẽ tự động cộng lại số lượng vào kho sản phẩm.")) return;
    fetch(`/admin/orders/cancel/${orderId}`, { method: 'POST' })
        .then(res => {
            if(res.ok) { alert("Đã hủy đơn và hoàn lại kho hàng."); location.reload(); }
            else { alert("Lỗi: Không thể hủy đơn hàng này."); }
        });
}
// Tự động đổi màu chữ của ô Select tương ứng với Option đang được chọn
const statusSelect = document.querySelector('.fa-select');
if (statusSelect) {
    const updateSelectColor = () => {
        const selectedOption = statusSelect.options[statusSelect.selectedIndex];
        statusSelect.style.color = selectedOption.style.color;
    };
    updateSelectColor();
    statusSelect.addEventListener('change', updateSelectColor);
}