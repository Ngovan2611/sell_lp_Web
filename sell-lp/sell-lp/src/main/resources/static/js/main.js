document.addEventListener("DOMContentLoaded", () => {

    /* =========================================
       SIDEBAR + OVERLAY
    ========================================= */

    const categoryBtn = document.getElementById("categoryBtn");
    const overlay = document.getElementById("overlay");
    const sidebar = document.getElementById("sidebar");

    function openSidebar() {
        if (!sidebar || !overlay) return;

        sidebar.classList.add("active");
        overlay.classList.add("active");

        document.body.style.overflow = "hidden";
    }

    function closeSidebar() {
        if (!sidebar || !overlay) return;

        sidebar.classList.remove("active");
        overlay.classList.remove("active");

        document.body.style.overflow = "auto";
    }

    if (categoryBtn) {

        categoryBtn.addEventListener("click", () => {

            if (sidebar.classList.contains("active")) {
                closeSidebar();
            } else {
                openSidebar();
            }

        });

    }

    if (overlay) {

        overlay.addEventListener("click", () => {
            closeSidebar();
        });

    }

    /* =========================================
       AUTO CLOSE SIDEBAR WHEN RESIZE DESKTOP
    ========================================= */

    window.addEventListener("resize", () => {

        if (window.innerWidth > 992) {
            closeSidebar();
        }

    });

    /* =========================================
       PRODUCT SCROLL ANIMATION
    ========================================= */

    const products = document.querySelectorAll(".product");

    if (products.length > 0) {

        const observer = new IntersectionObserver((entries) => {

            entries.forEach(entry => {

                if (entry.isIntersecting) {
                    entry.target.classList.add("show");
                }

            });

        }, {
            threshold: 0.15
        });

        products.forEach(product => {
            observer.observe(product);
        });

    }

    /* =========================================
       BANNER SLIDER
    ========================================= */

    const slides = document.querySelectorAll(".banner-slide");

    let currentSlide = 0;
    let slideInterval;

    function showSlide(index) {

        slides.forEach(slide => {
            slide.classList.remove("active");
        });

        slides[index].classList.add("active");
    }

    function nextSlide() {

        currentSlide++;

        if (currentSlide >= slides.length) {
            currentSlide = 0;
        }

        showSlide(currentSlide);
    }

    function startSlider() {

        if (slides.length <= 1) return;

        slideInterval = setInterval(() => {
            nextSlide();
        }, 2000);

    }

    function stopSlider() {

        clearInterval(slideInterval);

    }

    if (slides.length > 0) {

        showSlide(currentSlide);

        startSlider();

        /* Pause khi hover banner */

        const banner = document.querySelector(".banner");

        if (banner) {

            banner.addEventListener("mouseenter", () => {
                stopSlider();
            });

            banner.addEventListener("mouseleave", () => {
                startSlider();
            });

        }

    }

    /* =========================================
       SMOOTH SCROLL VIEW
    ========================================= */

    const scrollLinks = document.querySelectorAll('a[href^="#"]');

    scrollLinks.forEach(link => {

        link.addEventListener("click", function (e) {

            const targetId = this.getAttribute("href");

            if (targetId.length > 1) {

                const target = document.querySelector(targetId);

                if (target) {

                    e.preventDefault();

                    target.scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });

                }

            }

        });

    });

    /* =========================================
       FLOATING CONTACT TOGGLE
    ========================================= */

    const floatingContact = document.getElementById("floatingContact");

    window.toggleContact = function () {

        if (floatingContact) {
            floatingContact.classList.toggle("active");
        }

    };

    /* =========================================
       AUTO HIDE TOAST
    ========================================= */

    const toast = document.querySelector(".toast");

    if (toast) {

        setTimeout(() => {

            toast.style.opacity = "0";
            toast.style.transform = "translateX(100%)";

            setTimeout(() => {
                toast.remove();
            }, 500);

        }, 3000);

    }

});