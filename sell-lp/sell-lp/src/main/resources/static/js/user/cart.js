async function changeQty(btn, delta) {
    const input = btn.parentElement.querySelector('input');
    let value = Math.max(1, parseInt(input.value) + delta);

    const item = btn.closest('.cart-item');
    const cartItemId = item.dataset.cartItemId;
    const unitPrice = parseFloat(item.querySelector('.item-price').dataset.unitPrice);

    try {
        const res = await fetch(`/cart/update/${cartItemId}?quantity=${value}`, {
            method: 'POST'
        });

        const msg = await res.text();

        if (!res.ok) {
            showError(msg);
            return;
        }

        input.value = value;
        item.querySelector('.item-price').innerText =
            (value * unitPrice).toLocaleString('vi-VN') + 'đ';

        const checkbox = item.querySelector('.select-item');
        checkbox.dataset.price = value * unitPrice;

        if (checkbox.checked) updateTotal();

    } catch (e) {
        alert("Lỗi server");
    }
}
async function removeItem(btn) {
    const item = btn.closest('.cart-item');
    const cartItemId = item.dataset.cartItemId;
    const productName = item.querySelector('.item-info strong').innerText;

    const result = await Swal.fire({
        title: 'Xác nhận xóa?',
        text: `Bạn muốn xóa "${productName}" khỏi giỏ hàng?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Xóa ngay',
        cancelButtonText: 'Hủy'
    });

    if (result.isConfirmed) {
        try {
            const res = await fetch(`/cart/delete/${cartItemId}`, { method: 'DELETE' });
            if (res.ok) {
                item.remove();
                updateTotal();
                checkEmptyCart();
                Swal.fire('Đã xóa!', 'Sản phẩm đã rời khỏi giỏ hàng.', 'success');
            }
        } catch (e) {
            Swal.fire('Lỗi!', 'Không thể kết nối máy chủ.', 'error');
        }
    }
}
function checkEmptyCart() {
    const cartItems = document.querySelectorAll('.cart-item');
    const cartBox = document.getElementById('cartBox');

    if (cartItems.length === 0) {
        cartBox.innerHTML = `
            <div class="empty-cart fade-in">
                <i class="fa-solid fa-cart-shopping"></i>
                <p>Giỏ hàng của bạn đang trống</p>
            </div>
        `;

        document.getElementById('totalPrice').innerText = '0đ';
        document.getElementById('totalQuantity').innerText = '0';
    }
}

function updateTotal() {
    let totalPrice = 0, totalQuantity = 0;
    document.querySelectorAll('.cart-item').forEach(item => {
        const checkbox = item.querySelector('.select-item');
        const qty = parseInt(item.querySelector('.item-quantity input').value) || 0;
        const price = parseFloat(item.querySelector('.item-price').dataset.unitPrice) || 0;
        if (checkbox.checked) {
            totalPrice += qty * price;
            totalQuantity += qty;
        }
    });
    document.getElementById('totalPrice').innerText = totalPrice.toLocaleString('vi-VN') + 'đ';
    document.getElementById('totalQuantity').innerText = totalQuantity;
}
function showError(msg) {
    const box = document.getElementById("error-box");
    const text = document.getElementById("error-text");

    text.innerText = msg;
    box.classList.add("show");

    setTimeout(() => {
        box.classList.remove("show");
    }, 3000);
}
function goToCheckout() {
    const selectedItems = [];

    document.querySelectorAll('.cart-item').forEach(item => {
        const checkbox = item.querySelector('.select-item');
        if (checkbox.checked) {
            selectedItems.push(item.dataset.cartItemId);
        }
    });

    if (selectedItems.length === 0) {
        showError("Vui lòng chọn sản phẩm!");
        return;
    }

    window.location.href = "/checkout?ids=" + selectedItems.join(",");    }