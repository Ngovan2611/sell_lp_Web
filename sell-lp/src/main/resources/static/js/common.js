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
