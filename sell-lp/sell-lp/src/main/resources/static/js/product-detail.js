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
            const type = btn.dataset.color ? "color" : btn.dataset.ram ? "ram" : "rom";
            let valid = false;

            variants.forEach(v => {
                if (
                    (type !== "color" ? (!selected.color || v.dataset.color === selected.color) : true) &&
                    (type !== "ram" ? (!selected.ram || v.dataset.ram === selected.ram) : true) &&
                    (type !== "rom" ? (!selected.rom || v.dataset.rom === selected.rom) : true) &&
                    v.dataset[type] === btn.dataset[type] &&
                    Number(v.dataset.stock) > 0
                ) {
                    valid = true;
                }
            });

            btn.disabled = !valid;
            btn.style.opacity = valid ? "1" : "0.3";
            btn.style.cursor = valid ? "pointer" : "not-allowed";
        });
    });
}
window.onload = () => {
    const allVariants = document.querySelectorAll(".variant-item");
    const firstAvailableVariant = Array.from(allVariants).find(v => Number(v.dataset.stock) > 0);

    if (firstAvailableVariant) {
        selected.color = firstAvailableVariant.dataset.color;
        selected.ram = firstAvailableVariant.dataset.ram;
        selected.rom = firstAvailableVariant.dataset.rom;

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
    } else {
        updateVariant();
        updateAvailableOptions();
        const stockEl = document.getElementById("stock");
        if(stockEl) stockEl.innerText = "Hết hàng";
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
    const loadLimit = currentIndex + itemsPerLoad;
    for (; currentIndex < loadLimit && currentIndex < relatedProducts.length; currentIndex++) {
        const p = relatedProducts[currentIndex];
        const div = document.createElement("div");
        div.className = "product";

        div.innerHTML = `
            <img src="${p.imageUrl}">
            <h3>${p.name}</h3>
            <p>${p.description}</p>
            <a href="/product/${p.productId}" class="related-link-overlay">
                <button class="view-btn">Xem chi tiết</button>
            </a>
        `;
        container.appendChild(div);
    }

    const loadMoreBtn = document.getElementById("loadMoreBtn");
    if (loadMoreBtn && currentIndex >= relatedProducts.length) {
        loadMoreBtn.style.display = "none";
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
    const container = document.querySelector(".product-detail-container");
    const isActive = container.getAttribute("data-active") === "true";

    if (!isActive) {
        showError("Sản phẩm này hiện không còn kinh doanh!");
        return;
    }

    const btn = event.currentTarget;

    if (btn.disabled) return;
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
    const container = document.querySelector(".product-detail-container");
    const isActive = container.getAttribute("data-active") === "true";
    const isActiveAttr = container ? container.getAttribute("data-active") : "false";
    console.log("Trạng thái Active hiện tại trên HTML:", isActiveAttr);

    if (!isActive) {
        showError("Sản phẩm này hiện không còn kinh doanh!");
        return;
    }

    const btn = event.currentTarget;
    if (btn.disabled) return;
    const vId = document.getElementById("variantId").value;
    if (!vId) {
        showError("Vui lòng chọn phiên bản!");
        return;
    }

    window.location.href = `/buy-now?variantId=${vId}&qty=1`;
}