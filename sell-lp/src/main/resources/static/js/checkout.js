function openAddressPopup() {
    document.getElementById("overlay").style.display = "block";
    document.getElementById("addressPopup").style.display = "block";
    document.getElementById("selectAddressPopup").style.display = "none";
}

function openSelectAddressPopup() {
    document.getElementById("overlay").style.display = "block";
    document.getElementById("selectAddressPopup").style.display = "block";
    document.getElementById("addressPopup").style.display = "none";
}

function closeAddressPopup() {
    document.getElementById("overlay").style.display = "none";
    document.getElementById("addressPopup").style.display = "none";
}

function closeSelectAddressPopup() {
    document.getElementById("overlay").style.display = "none";
    document.getElementById("selectAddressPopup").style.display = "none";
}

document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
        closeAddressPopup();
    }
});

function selectAddressFromBtn(btn) {
    try {
        const name = btn.dataset.name || '';
        const phone = btn.dataset.phone || '';
        const address = btn.dataset.address || '';
        const addressId = btn.dataset.id || '';

        document.getElementById("selectedName").innerText = name;
        document.getElementById("selectedPhone").innerText = phone;
        document.getElementById("selectedAddress").innerText = address;

        const input = document.getElementById("selectedAddressId");
        if(input) {
            input.value = addressId;
        }

        closeSelectAddressPopup();
    } catch(e) {
        console.error("Error selecting address:", e);
        closeSelectAddressPopup();
    }
}
document.getElementById("overlay").addEventListener("click", function() {
    const addressPopup = document.getElementById("addressPopup");
    const selectAddressPopup = document.getElementById("selectAddressPopup");

    if (addressPopup.style.display === "block") {
        closeAddressPopup();
    } else if (selectAddressPopup.style.display === "block") {
        closeSelectAddressPopup();
    }
    closeEditAddressPopup();
});
function openEditAddressPopupFromBtn(btn) {
    const addr = {
        addressId: btn.dataset.id,
        recipientName: btn.dataset.name,
        phone: btn.dataset.phone,
        city: btn.dataset.city,
        district: btn.dataset.district,
        ward: btn.dataset.ward,
        street: btn.dataset.street
    };

    document.getElementById("overlay").style.display = "block";
    const popup = document.getElementById("editAddressPopup");
    popup.style.display = "block";

    document.getElementById("editAddressId").value = addr.addressId;
    document.getElementById("editRecipientName").value = addr.recipientName;
    document.getElementById("editPhone").value = addr.phone;
    document.getElementById("editCity").value = addr.city;
    document.getElementById("editDistrict").value = addr.district;
    document.getElementById("editWard").value = addr.ward;
    document.getElementById("editStreet").value = addr.street;
}

function closeEditAddressPopup() {
    document.getElementById("overlay").style.display = "none";
    document.getElementById("editAddressPopup").style.display = "none";
}
document.getElementById("editAddressForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);

    fetch(form.action, {
        method: "POST",
        body: formData,
        credentials: "same-origin"
    })
        .then(response => response.text())
        .then(data => {
            if(data === "Cập nhật thành công") {
                closeEditAddressPopup();

                const msg = document.getElementById("successMessage");
                msg.innerText = data;
                msg.style.display = "block";

                setTimeout(() => {
                    msg.style.display = "none";
                }, 3000);

                document.getElementById("selectedName").innerText = formData.get("recipientName");
                document.getElementById("selectedPhone").innerText = formData.get("phone");
                const fullAddress = `${formData.get("street")}, ${formData.get("ward")}, ${formData.get("district")}, ${formData.get("city")}`;
                document.getElementById("selectedAddress").innerText = fullAddress;

                document.getElementById("selectedAddressId").value = formData.get("addressId");
            } else {
                alert("Lỗi: " + data);
            }
        })
        .catch(err => {
            console.error(err);
            alert("Đã xảy ra lỗi khi cập nhật địa chỉ!");
        });
});
document.getElementById("orderForm").addEventListener("submit", function () {
    const selected = document.querySelector('input[name="paymentMethod"]:checked');
    document.getElementById("paymentMethodInput").value = selected.value;
});

document.getElementById("addAddressForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);

    fetch("/address/create", {
        method: "POST",
        body: data,
        credentials: "same-origin"
    })
        .then(res => {
            if(!res.ok) throw new Error("Lỗi server");
            return res.json(); // nhận JSON
        })
        .then(address => {
            const msgBox = document.getElementById("address-message");
            msgBox.innerText = "Thêm địa chỉ thành công!";
            msgBox.style.color = "green";

            form.reset();
            closeAddressPopup();

            // thêm địa chỉ vào popup chọn
            const container = document.querySelector(".address-list-container");
            const div = document.createElement("div");
            div.className = "address-item";
            div.innerHTML = `
            <p><b>${address.recipientName}</b> | <span>${address.phone}</span></p>
            <p>${address.fullAddress}</p>
            <div style="margin-top:8px;">
                <button type="button"
                        data-name="${address.recipientName}"
                        data-phone="${address.phone}"
                        data-address="${address.fullAddress}"
                        data-id="${address.addressId}"
                        onclick="selectAddressFromBtn(this)">Chọn</button>
                <button type="button"
                        data-id="${address.addressId}"
                        data-name="${address.recipientName}"
                        data-phone="${address.phone}"
                        data-city="${address.city}"
                        data-district="${address.district}"
                        data-ward="${address.ward}"
                        data-street="${address.street}"
                        onclick="openEditAddressPopupFromBtn(this)">Sửa</button>
            </div>
        `;
            container.appendChild(div);

            // tự chọn địa chỉ mới
            selectAddressFromBtn(div.querySelector("button"));
        })
        .catch(err => {
            const msgBox = document.getElementById("address-message");
            msgBox.innerText = "Có lỗi xảy ra!";
            msgBox.style.color = "red";
            console.error(err);
        });
});
document.getElementById("orderForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const selectedPayment = document.querySelector('input[name="paymentMethod"]:checked');
    if (!selectedPayment) {
        showError("Vui lòng chọn phương thức thanh toán!");
        return;
    }
    document.getElementById("paymentMethodInput").value = selectedPayment.value;

    const addressId = document.getElementById("selectedAddressId").value;
    if (!addressId || addressId === "" || addressId === "null") {
        alert("Bạn chưa chọn địa chỉ giao hàng. Vui lòng thêm hoặc chọn một địa chỉ!");
        openAddressPopup();
        return;
    }

    const form = e.target;
    const data = new FormData(form);

    console.log("Đang tạo đơn hàng với địa chỉ ID:", addressId);

    fetch("/order/create", {
        method: "POST",
        body: data
    })
        .then(res => {
            if(!res.ok) return res.text().then(text => { throw new Error(text) });
            return res.text();
        })
        .then(() => {
            showSuccessPopup();
        })
        .catch(err => {
            console.error("Lỗi tạo order:", err);
            alert("Đặt hàng thất bại: " + err.message);
        });
});
function showSuccessPopup() {
    const popup = document.getElementById("successPopup");

    popup.style.display = "flex";

    const content = popup.querySelector(".success-content");
    content.style.animation = "none";
    content.offsetHeight;
    content.style.animation = "";

    if (navigator.vibrate) {
        navigator.vibrate([50, 30, 50]);
    }

    setTimeout(() => {
        popup.style.display = "none";
        window.location.href = "/history-order";
    }, 2200);
}