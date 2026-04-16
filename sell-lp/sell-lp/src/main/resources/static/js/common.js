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