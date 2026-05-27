const toast = document.querySelector(".toast");

if (toast) {
    setTimeout(() => {
        toast.style.display = "none";
    }, 3000);
}

const btn = document.getElementById("categoryBtn");
const sidebar = document.getElementById("sidebar");
const overlay = document.getElementById("overlay");

if (btn && sidebar && overlay) {
    btn.addEventListener("click", () => {
        sidebar.classList.toggle("active");
        overlay.classList.toggle("active");
    });

    overlay.addEventListener("click", () => {
        sidebar.classList.remove("active");
        overlay.classList.remove("active");
    });
}
function toggleContact() {
    const wrapper = document.getElementById('floatingContact');
    const icon = document.getElementById('contactIcon');

    wrapper.classList.toggle('active');

    // Đổi icon từ comment sang dấu X
    if (wrapper.classList.contains('active')) {
        icon.className = 'fas fa-times';
    } else {
        icon.className = 'fas fa-comments';
    }
}

// Tự động đóng nếu người dùng nhấn ra ngoài
document.addEventListener('click', function(e) {
    const container = document.getElementById('floatingContact');
    if (!container.contains(e.target)) {
        container.classList.remove('active');
        document.getElementById('contactIcon').className = 'fas fa-comments';
    }
});

document.addEventListener('DOMContentLoaded', function() {
    // Chọn tất cả item, cả unread để người dùng click vẫn có phản hồi
    const notifList = document.getElementById('global-notif-list');

    if (!notifList) return;

    notifList.addEventListener('click', function(e) {
        // Kỹ thuật Event Delegation: bắt sự kiện click vào thẻ li hoặc con của li
        const item = e.target.closest('.notif-item.unread');
        if (!item) return;

        const notifId = item.getAttribute('data-id');

        fetch(`/api/notifications/mark-as-read/${notifId}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                // THÊM DÒNG NÀY VÀO - Đây là chìa khóa
                'X-Requested-With': 'XMLHttpRequest'
            },
            credentials: 'include'
        })
            .then(response => {
                if (response.status === 401) {
                    // Nếu bị 401, thông báo người dùng đăng nhập lại
                    alert("Phiên đăng nhập đã hết hạn!");
                    window.location.href = "/login";
                    return;
                }

                if (response.ok) {
                    // 1. Đổi 'element' thành 'item'
                    item.classList.remove('unread');
                    item.classList.add('read');

                    // 2. Cập nhật Badge số lượng thông báo
                    const badge = document.getElementById('global-notif-badge');
                    if (badge) {
                        let count = parseInt(badge.innerText);
                        if (count > 1) {
                            badge.innerText = count - 1;
                        } else {
                            badge.remove(); // Xóa badge nếu hết thông báo
                        }
                    }
                    console.log("Đã đánh dấu đọc thành công!");
                }
            })
            .catch(error => console.error('Error:', error));
    });
});