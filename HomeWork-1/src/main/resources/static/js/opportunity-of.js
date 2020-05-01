function opportunityOf(btnId, getURL) {
    console.log('opportunityOf');
    $.ajax({
        type: 'GET',
        url: getURL,
        dataType: "json",
        success: function (data) {
            console.log('opportunityOf inline function data: ' + data.count);
            if (data.count > 0) {
                $('#' + btnId).hide()
            }
        }
    })
}

