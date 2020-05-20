// Retrieve interest list when application starts
function searchUsers() {
    var firstName = $('#firstName').val()
    var surName = $('#surName').val()
    console.log('searchUsers');
    $.ajax({
        type: 'GET',
        url: '/user/api/search-users?firstName=' + firstName + '&surName=' + surName,
        dataType: "json", // data type of response
        contentType:"application/json; charset=utf-8",
        success: renderMessage
    });
}

function renderMessage(data) {
    console.log('renderMessage' + data);
}

