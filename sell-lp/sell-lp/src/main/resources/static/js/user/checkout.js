// ==========================================
// 1. CÁC HÀM ĐIỀU KHIỂN POPUP
// ==========================================
function openAddressPopup() {
    toggleOverlay(true);
    document.getElementById("addressPopup").style.display = "block";
    document.getElementById("selectAddressPopup").style.display = "none";
}

function openSelectAddressPopup() {
    toggleOverlay(true);
    document.getElementById("selectAddressPopup").style.display = "block";
    document.getElementById("addressPopup").style.display = "none";
}

function closeAddressPopup() {
    toggleOverlay(false);
    document.getElementById("addressPopup").style.display = "none";
}

function closeSelectAddressPopup() {
    toggleOverlay(false);
    document.getElementById("selectAddressPopup").style.display = "none";
}

function toggleOverlay(show) {
    document.getElementById("overlay").style.display = show ? "block" : "none";
}

// ==========================================
// 2. XỬ LÝ ĐỊA CHỈ (CHỌN, SỬA, THÊM)
// ==========================================
function selectAddressFromBtn(btn) {
    document.getElementById("selectedName").innerText = btn.dataset.name;
    document.getElementById("selectedPhone").innerText = btn.dataset.phone;
    document.getElementById("selectedAddress").innerText = btn.dataset.address;
    document.getElementById("selectedAddressId").value = btn.dataset.id;
    closeSelectAddressPopup();
}

function openEditAddressPopupFromBtn(btn) {
    toggleOverlay(true);
    document.getElementById("editAddressPopup").style.display = "block";

    document.getElementById("editAddressId").value = btn.dataset.id;
    document.getElementById("editRecipientName").value = btn.dataset.name;
    document.getElementById("editPhone").value = btn.dataset.phone;
    document.getElementById("editCity").value = btn.dataset.city;
    document.getElementById("editDistrict").value = btn.dataset.district;
    document.getElementById("editWard").value = btn.dataset.ward;
    document.getElementById("editStreet").value = btn.dataset.street;
}

function closeEditAddressPopup() {
    toggleOverlay(false);
    document.getElementById("editAddressPopup").style.display = "none";
}

// ==========================================
// 3. VALIDATE & SUBMIT (CHỐNG LỖI 400)
// ==========================================
document.getElementById("orderForm").addEventListener("submit", function(e) {
    const selectedPayment = document.querySelector('input[name="paymentMethod"]:checked');
    const addressId = document.getElementById("selectedAddressId").value;

    // Validate Địa chỉ
    if (!addressId || addressId === "" || addressId === "null") {
        e.preventDefault();
        alert("Vui lòng chọn hoặc thêm địa chỉ giao hàng!");
        openSelectAddressPopup();
        return;
    }

    // Validate Thanh toán
    if (!selectedPayment) {
        e.preventDefault();
        alert("Vui lòng chọn phương thức thanh toán!");
        return;
    }

    document.getElementById("paymentMethodInput").value = selectedPayment.value;

    // Hiệu ứng COD
    if (selectedPayment.value === "COD") {
        e.preventDefault();
        showSuccessPopup();
        setTimeout(() => { e.target.submit(); }, 2200);
    }
});

// Xử lý gửi form Sửa địa chỉ (AJAX)
document.getElementById("editAddressForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const form = e.target;

    fetch(form.action, { method: "POST", body: new FormData(form) })
        .then(res => res.text())
        .then(data => {
            if(data === "Cập nhật thành công") {
                closeEditAddressPopup();
                location.reload(); // Reload nhẹ để cập nhật lại danh sách nếu cần
            } else {
                alert("Lỗi: " + data);
            }
        });
});

// Xử lý gửi form Thêm địa chỉ (AJAX)
document.getElementById("addAddressForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const form = e.target;

    fetch("/address/create", { method: "POST", body: new FormData(form) })
        .then(res => res.json())
        .then(addr => {
            alert("Thêm địa chỉ thành công!");
            location.reload();
        })
        .catch(() => alert("Có lỗi xảy ra, vui lòng kiểm tra lại dữ liệu!"));
});

// ==========================================
// 4. TIỆN ÍCH KHÁC
// ==========================================
function showSuccessPopup() {
    const popup = document.getElementById("successPopup");
    popup.style.display = "flex";
    setTimeout(() => { window.location.href = "/history-order"; }, 2200);
}

document.getElementById("overlay").addEventListener("click", () => {
    closeAddressPopup();
    closeSelectAddressPopup();
    closeEditAddressPopup();
});

document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        closeAddressPopup();
        closeSelectAddressPopup();
        closeEditAddressPopup();
    }
});