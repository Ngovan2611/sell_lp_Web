
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