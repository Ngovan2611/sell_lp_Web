    // Khai báo biến toàn cục quản lý đối tượng Tagify trong Modal
    let modalTagifyInstance;

    document.addEventListener("DOMContentLoaded", function() {
        // Đọc trực tiếp từ biến toàn cục do HTML khởi tạo trước đó
        const allTagsFromDb = window.allTagsFromDb || [];

        const whitelist = allTagsFromDb.map(t => ({
            value: t.name,
            id: t.tagId
        }));


    const modalInput = document.getElementById('modalTagifyInput');
    modalTagifyInstance = new Tagify(modalInput, {
    whitelist: whitelist,
    dropdown: {
    maxItems: 15,
    classname: "tags-look",
    enabled: 0,
    closeOnSelect: false
}
});
});

    function toggleVariant(id) {
    const row = document.getElementById('variant-' + id);
    const icon = document.getElementById('icon-' + id);

    if (row.style.display === 'none' || row.style.display === '') {
    row.style.display = 'table-row';
    icon.style.transform = 'rotate(90deg)';
    icon.style.transition = '0.3s';
} else {
    row.style.display = 'none';
    icon.style.transform = 'rotate(0deg)';
}
}

    function openEditModal(id) {
    fetch(`/admin/products/${id}`)
        .then(res => res.json())
        .then(product => {
            document.getElementById('editProductId').value = product.productId;
            document.getElementById('editName').value = product.name;
            document.getElementById('editActive').value = product.active.toString();
            document.getElementById('editDescription').value = product.description || '';
            if (product.category) document.getElementById('editCategoryId').value = product.category.categoryId;
            document.getElementById('editImages').value = product.images ? product.images.join('\n') : '';
            renderPreviews();

            // =========================================================================
            // ĐỒNG BỘ ĐỔ DỮ LIỆU TAGS CỦA SẢN PHẨM VÀO TAGIFY KHI MỞ MODAL AJAX
            // =========================================================================
            modalTagifyInstance.removeAllTags(); // Làm sạch toàn bộ tag hiển thị của sản phẩm trước đó

            if (product.tags && product.tags.length > 0) {
                const currentProductTags = product.tags.map(tag => ({
                    value: tag.name,
                    id: tag.tagId
                }));
                modalTagifyInstance.addTags(currentProductTags); // Nạp mảng tag hiện tại vào ô nhập liệu
            }

            const container = document.getElementById('variantEditContainer');
            container.innerHTML = product.variants.map(v => `
                    <div class="variant-edit-row">
                        <input type="hidden" class="v-id" value="${v.variantId}">
                        <div class="v-label">
                            ${v.colorName || 'Mặc định'} ${v.ramSize ? '/ ' + v.ramSize : ''} ${v.romSize ? '/ ' + v.romSize : ''}
                        </div>
                        <input type="number" class="v-price form-control" value="${v.price}">
                        <input type="number" class="v-stock form-control" value="${v.stockQty}">
                    </div>
                `).join('');
            document.getElementById('editProductModal').style.display = 'block';
        });
}

    function closeModal() {
    document.getElementById('editProductModal').style.display = 'none';
}

    document.getElementById('modalFilePicker').onchange = async function(e) {
    const files = e.target.files;
    if (files.length === 0) return;

    const btn = e.target.nextElementSibling;
    const originalText = btn.innerHTML;

    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang tải...';
    btn.disabled = true;

    const formData = new FormData();
    for (let file of files) {
    formData.append('files', file);
}

    try {
    const res = await fetch('/admin/products/upload', {
    method: 'POST',
    body: formData
});

    if (res.ok) {
    const urls = await res.json();
    const textarea = document.getElementById('editImages');
    const existingUrls = textarea.value.trim();

    textarea.value = (existingUrls ? existingUrls + '\n' : '') + urls.join('\n');

    renderPreviews();
} else {
    alert("Lỗi khi upload ảnh lên Cloudinary!");
}
} catch (error) {
    console.error("Error:", error);
    alert("Không thể kết nối đến server!");
} finally {
    btn.innerHTML = originalText;
    btn.disabled = false;
    e.target.value = '';
}
};

    document.getElementById('editProductForm').onsubmit = function(e) {
    e.preventDefault();
    const productId = parseInt(document.getElementById('editProductId').value);
    const variants = Array.from(document.querySelectorAll('.variant-edit-row')).map(row => ({
    variantId: parseInt(row.querySelector('.v-id').value),
    price: parseFloat(row.querySelector('.v-price').value),
    stockQty: parseInt(row.querySelector('.v-stock').value)
}));

    // =========================================================================
    // THU THẬP DANH SÁCH MẢNG {tagId, name} THEO ĐÚNG CẤU TRÚC TagRequest DTO (ĐÃ SỬA)
    // =========================================================================
    const currentSelectedTags = modalTagifyInstance.value || [];
    const tagsPayload = currentSelectedTags.map(item => ({
    tagId: item.id ? parseInt(item.id) : null, // Gán id nếu có sẵn trong DB, ngược lại để null
    name: item.value.trim()                     // Luôn lấy text name thuần túy (ví dụ: "tya")
}));

    const data = {
    productId: productId,
    name: document.getElementById('editName').value,
    active: document.getElementById('editActive').value === 'true',
    categoryId: parseInt(document.getElementById('editCategoryId').value) || null,
    description: document.getElementById('editDescription').value,
    imageUrls: document.getElementById('editImages').value.split('\n').map(u => u.trim()).filter(u => u),
    variants: variants,
    tagIds: tagsPayload // Gửi mảng Object lên thuộc tính 'tagIds' tương thích với DTO
};

    fetch(`/admin/products/${productId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
}).then(res => {
    if (res.ok) {
    alert('Cập nhật thành công!');
    location.reload();
} else {
    alert('Lỗi dữ liệu khi cập nhật sản phẩm!');
}
});
};

    function renderPreviews() {
    const textarea = document.getElementById('editImages');
    const container = document.getElementById('imagePreviewContainer');
    const urls = textarea.value.split('\n').map(u => u.trim()).filter(u => u !== "");

    container.innerHTML = "";

    if (urls.length === 0) {
    container.innerHTML = '<span class="text-muted" style="font-size: 0.8rem;">Chưa có ảnh nào...</span>';
    return;
}

    urls.forEach((url, index) => {
    const wrapper = document.createElement('div');
    wrapper.style.cssText = `
                position: relative;
                min-width: 80px;
                width: 80px;
                height: 80px;
                flex-shrink: 0;
            `

    wrapper.innerHTML = `
                <img src="${url}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                <button type="button" onclick="removeImage(${index})"
                    style="position: absolute; top: -8px; right: -8px; background: #ff4757; color: white; border: none; border-radius: 50%; width: 22px; height: 22px; font-size: 12px; cursor: pointer; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 4px rgba(0,0,0,0.2);">
                    <i class="fas fa-times"></i>
                </button>
                ${index === 0 ? '<span style="position:absolute; bottom:0; left:0; right:0; background:rgba(0,123,255,0.8); color:white; font-size:9px; text-align:center; border-radius:0 0 4px 4px;">Ảnh chính</span>' : ''}
            `;
    container.appendChild(wrapper);
});
}

    function removeImage(index) {
    const textarea = document.getElementById('editImages');
    let urls = textarea.value.split('\n').map(u => u.trim()).filter(u => u !== "");
    urls.splice(index, 1);
    textarea.value = urls.join('\n');
    renderPreviews();
}

    document.getElementById('editImages').addEventListener('input', renderPreviews);
