let addRow = function () {
    let listName = 'interests'; // list name in Interest.class
    let fieldsNames = ['id', 'interest']; // field names from Movie.class
    let rowIndex = document.querySelectorAll('.item').length; //we can add mock class to each movie-row

    let row = document.createElement('div');
    row.classList.add('row', 'item');

    fieldsNames.forEach((fieldName) => {
        let col = document.createElement('div');
        col.classList.add('col', 'form-group');
        if (fieldName === 'id') {
            col.classList.add('d-none'); // field with id - hidden
        }

        let input = document.createElement('input');
        input.type = 'text';
        input.classList.add('form-control');
        input.id = listName + rowIndex + '.' + fieldName;
        input.setAttribute('name', listName + '[' + rowIndex + '].' + fieldName);

        col.appendChild(input);
        row.appendChild(col);
    });

    document.getElementById('interestList').appendChild(row);
};
