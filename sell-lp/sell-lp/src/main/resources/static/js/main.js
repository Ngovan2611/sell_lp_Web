document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("categoryBtn");
    const overlay = document.getElementById("overlay");
    const sidebar = document.getElementById("sidebar");

    if (btn && overlay && sidebar) {

        btn.addEventListener("click", () => {
            overlay.classList.add("active");
            sidebar.classList.add("highlight");
            document.body.style.overflow = "hidden";

        });

        overlay.addEventListener("click", () => {
            overlay.classList.remove("active");
            sidebar.classList.remove("highlight");
            document.body.style.overflow = "auto";

        });

    }





    const products = document.querySelectorAll(".product");

    const observer = new IntersectionObserver((entries) => {

        entries.forEach(entry => {

            if (entry.isIntersecting) {
                entry.target.classList.add("show");
            }

        });

    }, {
        threshold: 0.2
    });

    products.forEach(product => observer.observe(product));

    /* ===== BANNER SLIDER ===== */

    const slides = document.querySelectorAll(".banner-slide");

    let currentSlide = 0;

    function showSlide(index) {

        slides.forEach(slide => {
            slide.classList.remove("active");
        });

        slides[index].classList.add("active");

    }

    setInterval(() => {

        currentSlide++;

        if (currentSlide >= slides.length) {
            currentSlide = 0;
        }

        showSlide(currentSlide);

    }, 2000);
});
