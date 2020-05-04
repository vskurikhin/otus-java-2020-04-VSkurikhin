// Retrieve interest list when application starts
function addFriend(id, friendId) {
    console.log('addFriend');
    $.ajax({
        type: 'POST',
        url: '/user/api/add-friend/' + id,
        dataType: "json", // data type of response
        contentType:"application/json; charset=utf-8",
        data: idToJSON(friendId),
        success: renderMessage
    });
}

function idToJSON(id) {
    return JSON.stringify({
        "friendId": Number(id)
    });
}


function renderMessage(data) {
    console.log('renderMessage' + data);
}

