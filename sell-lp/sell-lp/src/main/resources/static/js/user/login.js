
    function togglePassword() {
    const input = document.getElementById("password");
    input.type = input.type === "password" ? "text" : "password";
}

    window.addEventListener("DOMContentLoaded", () => {
    const errorBox = document.getElementById("error-message");
    if (errorBox) {
    requestAnimationFrame(() => errorBox.classList.add("show"));
    setTimeout(() => {
    errorBox.classList.remove("show");
}, 3000);
}
});
