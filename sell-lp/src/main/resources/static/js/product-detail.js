let selected = {
    color: null,
    ram: null,
    rom: null
};

function selectOption(btn, type) {
    if (selected[type] === btn.dataset[type]) {
        selected[type] = null;
        btn.classList.remove("active");
    } else {
        selected[type] = btn.dataset[type];

        // active UI
        btn.parentElement.querySelectorAll("button")
            .forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
    }

    updateVariant();
    updateAvailableOptions();
}

function updateVariant() {
    const variants = document.querySelectorAll(".variant-item");

    let found = null;

    variants.forEach(v => {
        let match = true;
        for (let key in selected) {
            if (selected[key] && v.dataset[key] !== selected[key]) {
                match = false;
                break;
            }
        }
        if (match &&
            Object.values(selected).every(val => val !== null)) { // chỉ khi đã chọn hết mới coi là "found"
            found = v;
        }
    });

    const priceEl = document.getElementById("price");
    const stockEl = document.getElementById("stock");

    if (found) {
        priceEl.innerText = Number(found.dataset.price).toLocaleString() + "đ";
        stockEl.innerText = "Còn lại: " + found.dataset.stock;
        document.getElementById("variantId").value = found.dataset.id;
    } else {
        let prices = Array.from(variants).map(v => Number(v.dataset.price));
        let minPrice = Math.min(...prices);
        let maxPrice = Math.max(...prices);

        if (minPrice === maxPrice) {
            priceEl.innerText = minPrice.toLocaleString() + "đ";
        } else {
            priceEl.innerText = `${minPrice.toLocaleString()}đ - ${maxPrice.toLocaleString()}đ`;
        }

        stockEl.innerText = "";
        document.getElementById("variantId").value = "";
    }
}
function updateAvailableOptions() {
    const variants = document.querySelectorAll(".variant-item");

    document.querySelectorAll(".variant-group").forEach(group => {
        group.querySelectorAll("button").forEach(btn => {

            const type =
                btn.dataset.color ? "color" :
                    btn.dataset.ram ? "ram" : "rom";

            let valid = false;

            variants.forEach(v => {
                if (
                    (type !== "color" ? (!selected.color || v.dataset.color === selected.color) : true) &&
                    (type !== "ram" ? (!selected.ram || v.dataset.ram === selected.ram) : true) &&
                    (type !== "rom" ? (!selected.rom || v.dataset.rom === selected.rom) : true) &&
                    v.dataset[type] === btn.dataset[type]
                ) {
                    valid = true;
                }
            });

            btn.disabled = !valid;
            btn.style.opacity = valid ? "1" : "0.3";
        });
    });
}
window.onload = () => {
    const firstVariant = document.querySelector(".variant-item");

    if (firstVariant) {
        selected.color = firstVariant.dataset.color;
        selected.ram = firstVariant.dataset.ram;
        selected.rom = firstVariant.dataset.rom;

        // active UI
        document.querySelectorAll("[data-color]").forEach(b => {
            if (b.dataset.color === selected.color) b.classList.add("active");
        });

        document.querySelectorAll("[data-ram]").forEach(b => {
            if (b.dataset.ram === selected.ram) b.classList.add("active");
        });

        document.querySelectorAll("[data-rom]").forEach(b => {
            if (b.dataset.rom === selected.rom) b.classList.add("active");
        });

        updateVariant();
        updateAvailableOptions();
    }
};

const container = document.getElementById("relatedProducts");
const dataDivs = document.querySelectorAll("#relatedProductsData > div");
const relatedProducts = Array.from(dataDivs).map(d => ({
    productId: d.dataset.id,
    name: d.dataset.name,
    description: d.dataset.desc,
    imageUrl: d.dataset.img
}));

let currentIndex = 0;
const itemsPerLoad = 5;

function renderProducts() {
    for (let i = 0; i < itemsPerLoad && currentIndex < relatedProducts.length; i++, currentIndex++) {
        const p = relatedProducts[currentIndex];
        const div = document.createElement("div");
        div.className = "product";
        div.innerHTML = `
            <img src="${p.imageUrl}">
            <h3>${p.name}</h3>
            <p>${p.description}</p>
            <a href="/product/${p.productId}">
                <button class="view-btn">Xem chi tiết</button>
            </a>
        `;
        container.appendChild(div);
    }

    if (currentIndex >= relatedProducts.length) {
        document.getElementById("loadMoreBtn").style.display = "none";
    }
}

renderProducts();
document.getElementById("loadMoreBtn").addEventListener("click", renderProducts);

function addToCart() {
    const variantId = document.getElementById("variantId").value;

    if (!variantId) {
        showError("Vui lòng chọn phiên bản!");
        return;
    }

    fetch('/cart/add?variantId=' + variantId, {
        method: 'POST'
    })
        .then(res => {
            if (res.status === 401) {
                window.location.href = "/login";
                return;
            }
            return res.text();
        })
        .then(data => {
            if (data === "SUCCESS") {
                showSuccess("Đã thêm vào giỏ hàng");
            } else {
                showError(data);
            }
        })
        .catch(() => showError("Lỗi hệ thống"));
}

function showSuccess(message) {
    const box = document.getElementById("success-box");
    const text = document.getElementById("success-text");

    text.innerText = message;
    box.style.display = "block";
    box.classList.add("show");

    setTimeout(() => {
        box.classList.remove("show");
        setTimeout(() => box.style.display = "none", 300);
    }, 3000);
}

function showError(message) {
    const box = document.getElementById("error-message");
    if (box) {
        box.querySelector("span").innerText = message;
        box.classList.add("show");
        setTimeout(() => box.classList.remove("show"), 3000);
    } else {
        alert(message);
    }
}
function buyNow() {
    const vId = document.getElementById("variantId").value;
    if (!vId) {
        showError("Vui lòng chọn phiên bản!");
        return;
    }
    // Chuyển hướng thẳng đến /buy-now
    window.location.href = `/buy-now?variantId=${vId}&qty=1`;
}