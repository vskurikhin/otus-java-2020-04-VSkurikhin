let addRow = function () {
    var listName = 'interests'; // list name in Interest.class
    var fieldsNames = ['id', 'interest']; // field names from Movie.class
    var rowIndex = document.querySelectorAll('.item').length; //we can add mock class to each movie-row

    var row = document.createElement('div');
    row.classList.add('row', 'item');

    // noinspection BadExpressionStatementJS
    fieldsNames.forEach((fieldName) => {
        var col = document.createElement('div');
        if (fieldName === 'id') {
            col.classList.add('hidden'); // field with id - hidden
            col.classList.add('col-md-6', 'form-group');
        } else {
            col.classList.add('col-md-4', 'form-group');
        }

        var input = document.createElement('input');
        input.type = 'text';
        input.classList.add('form-control');
        input.id = listName + rowIndex + '.' + fieldName;
        input.setAttribute('name', listName + '[' + rowIndex + '].' + fieldName);

        col.appendChild(input);
        row.appendChild(col);
    });

    document.getElementById('interestList').appendChild(row);
};
