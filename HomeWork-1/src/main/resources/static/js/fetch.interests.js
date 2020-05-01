// Retrieve interest list when application starts
function findAllInterest(id) {
    console.log('findAllInterest');
    $.ajax({
        type: 'GET',
        url: '/user/interests/' + id,
        dataType: "json", // data type of response
        success: renderList
    });
}

function find() {
    findAll();
}

function renderList(data) {
    // JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
    var list = data == null ? [] : (data instanceof Array ? data : [data]);
    var separator = ' ';

    $.each(list, function(index, interest) {
        $('#interest-' + interest.userInfoId).append(separator + "<span>" + interest.interest + "</span>");
        separator = ',';
    });
}

