/**
 * File: banner.js
 * Quản lý tương tác REST API và điều khiển UI các Modal Banner
 */

// Lắng nghe sự kiện xử lý Form Thêm Mới bằng Fetch API
document.addEventListener("DOMContentLoaded", function () {
    const addForm = document.getElementById('addForm');
    if (addForm) {
        addForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new FormData(this);

            fetch(this.action, {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    alert(data.message);
                    if (data.success) {
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error("Error saving banner:", error);
                    alert("Đã xảy ra lỗi kết nối khi thêm mới banner!");
                });
        });
    }

    // Lắng nghe sự kiện xử lý Form Cập Nhật bằng Fetch API
    const editForm = document.getElementById('editForm');
    if (editForm) {
        editForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new FormData(this);

            fetch(this.action, {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    alert(data.message);
                    if (data.success) {
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error("Error updating banner:", error);
                    alert("Đã xảy ra lỗi kết nối khi cập nhật banner!");
                });
        });
    }
});

// Hàm cốt lõi: Tự động upload ảnh bằng Ajax ngay khi User chọn ảnh từ máy tính
function uploadAndPreview(input, previewImgId, previewBoxId, hiddenInputId, submitBtnId, statusId) {
    const file = input.files[0];
    if (!file) return;

    const previewBox = document.getElementById(previewBoxId);
    const previewImg = document.getElementById(previewImgId);
    const submitBtn = document.getElementById(submitBtnId);
    const statusLabel = document.getElementById(statusId);

    previewBox.style.display = 'block';
    previewImg.style.opacity = '0.4';
    statusLabel.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang tải tập tin lên hệ thống...';

    const reader = new FileReader();
    reader.onload = function(e) {
        previewImg.src = e.target.result;
    };
    reader.readAsDataURL(file);

    submitBtn.disabled = true;
    submitBtn.style.opacity = '0.5';

    const formData = new FormData();
    formData.append("files", file);

    fetch('/admin/banners/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error("Lỗi tải lên");
            return response.json();
        })
        .then(urls => {
            if(urls && urls.length > 0) {
                const cloudUrl = urls[0];
                document.getElementById(hiddenInputId).value = cloudUrl;

                previewImg.src = cloudUrl;
                previewImg.style.opacity = '1';
                statusLabel.innerHTML = '<i class="fas fa-check-circle"></i> Tải ảnh lên thành công!';
                statusLabel.style.color = '#10b981';

                submitBtn.disabled = false;
                submitBtn.style.opacity = '1';
            }
        })
        .catch(error => {
            console.error("Upload error: ", error);
            statusLabel.innerHTML = '<i class="fas fa-times-circle"></i> Tải ảnh thất bại!';
            statusLabel.style.color = '#ef4444';
            previewImg.style.opacity = '1';
            alert('Có lỗi xảy ra trong quá trình upload ảnh, vui lòng thử lại!');
        });
}

// Gọi API thay đổi nhanh trạng thái Ẩn / Hiện của Banner
function toggleBannerStatus(bannerId) {
    fetch(`/admin/banners/toggle/${bannerId}`, {
        method: 'POST'
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                window.location.reload();
            }
        })
        .catch(error => {
            console.error("Toggle error:", error);
            alert("Thao tác ẩn hiện gặp lỗi kết nối!");
        });
}

// Gọi API xóa bỏ hoàn toàn Banner khỏi hệ thống cơ sở dữ liệu
function deleteBanner(bannerId) {
    if (!confirm('Bạn có chắc chắn muốn xóa banner này?')) return;

    fetch(`/admin/banners/delete/${bannerId}`, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                window.location.reload();
            }
        })
        .catch(error => {
            console.error("Delete error:", error);
            alert("Thao tác xóa banner gặp lỗi kết nối!");
        });
}

function openAddModal() {
    document.getElementById('addForm').reset();
    document.getElementById('addUploadedUrl').value = '';
    document.getElementById('addPreviewBox').style.display = 'none';
    document.getElementById('addPreview').src = '';

    const submitBtn = document.getElementById('addSubmitBtn');
    submitBtn.disabled = false;
    submitBtn.style.opacity = '1';

    document.getElementById('addBannerModal').style.display = 'flex';
}

function openEditModal(button) {
    const id = button.getAttribute('data-id');
    if (!id || id === 'null') {
        alert('Lỗi: Không tìm thấy ID hợp lệ cho banner này!');
        return;
    }

    const title = button.getAttribute('data-title');
    const link = button.getAttribute('data-link');
    const image = button.getAttribute('data-image');
    const active = button.getAttribute('data-active') === 'true';

    document.getElementById('editFileForm').value = '';
    document.getElementById('editPreviewBox').style.display = 'none';
    document.getElementById('editNewPreview').src = '';

    const submitBtn = document.getElementById('editSubmitBtn');
    submitBtn.disabled = false;
    submitBtn.style.opacity = '1';

    document.getElementById('editForm').action = '/admin/banners/edit/' + id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editLinkUrl').value = link;
    document.getElementById('editPreview').src = image;
    document.getElementById('editUploadedUrl').value = image;
    document.getElementById('editActive').checked = active;

    document.getElementById('editBannerModal').style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

window.onclick = function(event) {
    if (event.target.classList.contains('custom-modal')) {
        event.target.style.display = 'none';
    }
}