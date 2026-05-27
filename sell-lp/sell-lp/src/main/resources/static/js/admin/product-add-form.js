let variantIdx = 0;

// Đọc dữ liệu an toàn từ biến toàn cục do file HTML truyền sang
const colors = window.productColors || [];
const rams = window.productRams || [];
const roms = window.productRoms || [];

// Tách biệt logic cập nhật trạng thái Ẩn/Hiện của các cột Specs tiêu đề
function updateHeaderVisibility(isTechProduct) {
    const headerSpecs = document.querySelectorAll('thead .spec-column');
    headerSpecs.forEach(col => {
        if (isTechProduct) {
            col.classList.remove('d-none');
        } else {
            col.classList.add('d-none');
        }
    });
}

// Hàm thêm dòng biến thể mới
function addVariant() {
    const categorySelect = document.getElementById('categoryId');
    let isTechProduct = false;

    if (categorySelect && categorySelect.options.length > 0) {
        const selectedOption = categorySelect.options[categorySelect.selectedIndex];
        isTechProduct = selectedOption.dataset.hasSpecs === 'true';
    }

    const displayClass = isTechProduct ? '' : 'd-none';
    const tbody = document.getElementById('variant-items');
    const row = document.createElement('tr');

    row.innerHTML = `
        <td>
            <select name="variants[${variantIdx}].colorId" class="form-input">
                ${colors.map(c => `<option value="${c.colorId}">${c.colorName}</option>`).join('')}
            </select>
        </td>
        <td class="spec-column ${displayClass}">
            <select name="variants[${variantIdx}].ramId" class="form-input">
                <option value="">N/A</option>
                ${rams.map(r => `<option value="${r.ramId}">${r.ramSize}</option>`).join('')}
            </select>
        </td>
        <td class="spec-column ${displayClass}">
            <select name="variants[${variantIdx}].romId" class="form-input">
                <option value="">N/A</option>
                ${roms.map(r => `<option value="${r.romId}">${r.romSize}</option>`).join('')}
            </select>
        </td>
        <td><input type="number" name="variants[${variantIdx}].price" min="0" class="form-input" placeholder="Giá" required></td>
        <td><input type="number" name="variants[${variantIdx}].stockQty" min="0" class="form-input" placeholder="Kho" required></td>
        <td>
            <button type="button" class="btn-remove" onclick="this.closest('tr').remove()">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
    updateHeaderVisibility(isTechProduct);
    variantIdx++;
}

// Lắng nghe sự kiện tải trang hoàn tất để khởi tạo DOM
document.addEventListener("DOMContentLoaded", function() {
    // Tạo hàng biến thể mặc định ban đầu dựa theo danh mục đầu tiên chọn sẵn
    addVariant();

    // Lắng nghe sự kiện thay đổi Danh mục để reset bảng biến thể
    const categoryIdEl = document.getElementById('categoryId');
    if (categoryIdEl) {
        categoryIdEl.addEventListener('change', function() {
            const tbody = document.getElementById('variant-items');
            tbody.innerHTML = '';
            variantIdx = 0;
            addVariant();
        });
    }

    // Lắng nghe sự kiện xử lý tải ảnh lên Cloudinary/Server
    const filePickerEl = document.getElementById('filePicker');
    if (filePickerEl) {
        filePickerEl.addEventListener('change', async function(e) {
            const files = e.target.files;
            if (files.length === 0) return;

            const status = document.getElementById('uploadStatus');
            const previewDiv = document.getElementById('singlePreview');
            const previewImg = document.getElementById('previewImg');
            const finalInput = document.getElementById('finalImageUrl');

            status.innerText = "Đang xử lý ảnh...";
            status.style.color = "#666";

            const formData = new FormData();
            formData.append('files', files[0]);

            try {
                const res = await fetch('/admin/products/upload', {
                    method: 'POST',
                    body: formData
                });

                if (res.ok) {
                    const urls = await res.json();
                    const uploadedUrl = urls[0];

                    finalInput.value = uploadedUrl;
                    previewImg.src = uploadedUrl;
                    previewDiv.classList.remove('d-none');

                    status.innerText = "Tải ảnh thành công!";
                    status.style.color = "green";
                } else {
                    status.innerText = "Lỗi khi tải ảnh lên server!";
                    status.style.color = "red";
                }
            } catch (error) {
                console.error("Error:", error);
                status.innerText = "Lỗi kết nối server!";
                status.style.color = "red";
            }
        });
    }

    // =========================================================================
    // KHỞI TẠO VÀ ĐỒNG BỘ DỮ LIỆU ĐỐI TƯỢNG TAGIFY
    // =========================================================================
    const allTagsFromDb = window.allTagsFromDb || [];
    const whitelist = allTagsFromDb.map(t => ({
        value: t.name,
        id: t.tagId
    }));

    const input = document.getElementById('tagifyInput');
    if (input) {
        const tagify = new Tagify(input, {
            whitelist: whitelist,
            maxTags: 10,
            dropdown: {
                maxItems: 20,
                classname: "tags-look",
                enabled: 0,
                closeOnSelect: false
            }
        });

        // Đồng bộ dữ liệu tag sang dạng mảng đối tượng gửi lên Spring DTO
        tagify.on('change', function() {
            const selectedItems = tagify.value || [];
            const container = document.getElementById('hiddenTagsContainer');
            if (!container) return;

            container.innerHTML = '';

            selectedItems.forEach((item, index) => {
                const idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = `tagIds[${index}].tagId`;
                idInput.value = item.id ? item.id : '';
                container.appendChild(idInput);

                const nameInput = document.createElement('input');
                nameInput.type = 'hidden';
                nameInput.name = `tagIds[${index}].name`;
                nameInput.value = item.value.trim();
                container.appendChild(nameInput);
            });
        });
    }
});

// Hàm kiểm tra trùng lặp cấu hình biến thể trên Giao diện khi submit form
function validateVariants() {
    const rows = document.querySelectorAll('#variant-items tr');

    if (rows.length === 0) {
        alert("Vui lòng thêm ít nhất một biến thể cho sản phẩm!");
        return false;
    }

    const uniqueKeys = new Set();
    let hasDuplicate = false;

    rows.forEach((row) => {
        const colorSelect = row.querySelector(`select[name^="variants["][name$=".colorId"]`);
        const ramSelect = row.querySelector(`select[name^="variants["][name$=".ramId"]`);
        const romSelect = row.querySelector(`select[name^="variants["][name$=".romId"]`);

        const colorId = colorSelect ? colorSelect.value : '';
        const isRamHidden = ramSelect ? ramSelect.closest('td').classList.contains('d-none') : true;
        const isRomHidden = romSelect ? romSelect.closest('td').classList.contains('d-none') : true;

        const ramId = (!isRamHidden && ramSelect) ? ramSelect.value : '';
        const romId = (!isRomHidden && romSelect) ? romSelect.value : '';

        const variantKey = `${colorId}_${ramId}_${romId}`;

        if (uniqueKeys.has(variantKey)) {
            hasDuplicate = true;
            if(colorSelect) colorSelect.style.borderColor = '#ef4444';
            if(!isRamHidden && ramSelect) ramSelect.style.borderColor = '#ef4444';
            if(!isRomHidden && romSelect) romSelect.style.borderColor = '#ef4444';
        } else {
            uniqueKeys.add(variantKey);
            if(colorSelect) colorSelect.style.borderColor = '';
            if(ramSelect) ramSelect.style.borderColor = '';
            if(romSelect) romSelect.style.borderColor = '';
        }
    });

    if (hasDuplicate) {
        alert("⚠️ Lỗi: Có các biến thể bị trùng lặp cấu hình (Màu sắc, RAM, ROM). Vui lòng kiểm tra lại các ô đánh dấu đỏ!");
        return false;
    }

    return true;
}