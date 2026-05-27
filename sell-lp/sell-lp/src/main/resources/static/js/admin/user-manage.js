
    /* =========================
    TOGGLE USER STATUS
    ========================= */

    function handleToggle(button) {

    const userId = button.getAttribute('data-id');

    if (confirm("Bạn có chắc chắn muốn thay đổi trạng thái người dùng này?")) {

    fetch(`/admin/users/toggle-status/${userId}`, {
    method: 'POST'
})
    .then(response => {

    if (response.ok) {
    location.reload();
} else {
    alert("Có lỗi xảy ra.");
}

})
    .catch(error => {

    console.error(error);

    alert("Lỗi máy chủ.");

});

}

}

    /* =========================
    MODAL
    ========================= */

    function openNotifyModal(button){

    const modal = document.getElementById("notifyModal");

    document.getElementById("notifyUserId").value =
    button.dataset.id;

    document.getElementById("notifyUserName").value =
    button.dataset.name;

    modal.classList.add("active");
}

    function closeNotifyModal(){

    document.getElementById("notifyModal")
        .classList.remove("active");

}

    /* =========================
    SEND NOTIFICATION
    ========================= */

    function sendNotification(){

    const userId =
    document.getElementById("notifyUserId").value;

    const title =
    document.getElementById("notifyTitle").value;

    const message =
    document.getElementById("notifyMessage").value;

    if(!title || !message){

    alert("Vui lòng nhập đầy đủ.");

    return;
}

    fetch(`/admin/notifications/send/${userId}`,{

    method:'POST',

    headers:{
    'Content-Type':'application/json'
},

    body:JSON.stringify({

    title:title,
    message:message,
    type:'OTHER'

})

})
    .then(response => {

    if(response.ok){

    alert("Gửi thông báo thành công.");

    closeNotifyModal();

}else{

    alert("Không thể gửi thông báo.");

}

})
    .catch(error => {

    console.error(error);

    alert("Lỗi máy chủ.");

});

}

