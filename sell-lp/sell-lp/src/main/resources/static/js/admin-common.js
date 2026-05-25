
    document.querySelectorAll('.has-submenu > a').forEach(menu => {

    menu.addEventListener('click', function (e) {

        e.preventDefault();

        const parent = this.parentElement;

        // đóng menu khác
        document.querySelectorAll('.has-submenu').forEach(item => {

            if (item !== parent) {
                item.classList.remove('open');
            }

        });

        // toggle menu hiện tại
        parent.classList.toggle('open');

    });

});
    /* ==========================================================================
       TỰ ĐỘNG ĐÓNG TẤT CẢ CÁC LOẠI MODAL KHI CLICK RA NGOÀI VÙNG CHỨA
       ========================================================================== */
    window.addEventListener('click', function(event) {
        // 1. Xử lý cho Modal Chỉnh sửa sản phẩm
        const productModal = document.getElementById("editProductModal");
        if (event.target === productModal) {
            closeModal(); // Gọi hàm đóng modal có sẵn trong file HTML của bạn
        }

        // 2. Xử lý cho Modal Thông báo (bên trang quản lý user)
        const notifyModal = document.getElementById("notifyModal");
        if (event.target === notifyModal) {
            if (typeof closeNotifyModal === "function") {
                closeNotifyModal();
            } else {
                notifyModal.style.display = 'none';
            }
        }
    });