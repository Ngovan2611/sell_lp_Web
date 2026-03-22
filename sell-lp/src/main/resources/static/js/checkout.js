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