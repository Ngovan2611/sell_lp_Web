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

    // Tìm variant khớp hoàn toàn
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
        // Chưa chọn hết option, hiển thị khoảng giá
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

// lần đầu render 5 sản phẩm
renderProducts();
document.getElementById("loadMoreBtn").addEventListener("click", renderProducts);
