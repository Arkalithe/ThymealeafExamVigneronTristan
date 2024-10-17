function deleteCapital(button) {
    const capitalId = button.getAttribute("data-id");

    if (confirm("Êtes-vous sûr de vouloir supprimer cette capitale ?")) {
        $.ajax({
            url: `/api/capitals/delete/${capitalId}`,
            type: 'DELETE',
            success: function (result) {
                alert("Capitale supprimée avec succès.");
                window.location.reload();
            },
            error: function (err) {
                alert("Erreur lors de la suppression.");
            }
        });
    }
}

document.getElementById('toggleView').addEventListener('click', function () {
    const table = document.getElementById('capitalTable');
    const tableBody = table.querySelector('tbody');
    const isCardView = tableBody.classList.contains('card-view');

    if (!isCardView) {
        const rows = Array.from(tableBody.querySelectorAll('tr'));
        tableBody.innerHTML = '';

        tableBody.classList.add('card-view');

        rows.forEach(function (row) {
            const cells = row.querySelectorAll('td');
            const card = document.createElement('div');
            card.classList.add('card');

            const img = cells[0].querySelector('img');
            const name = cells[1].textContent;
            const latitude = cells[2].textContent;
            const longitude = cells[3].textContent;
            const people = cells[4].textContent;

            card.innerHTML = `
                <img src="${img.src}" alt="${name}">
                <h5>${name}</h5>
                <p>Latitude: ${latitude}</p>
                <p>Longitude: ${longitude}</p>
                <p>Population: ${people}</p>
            `;
            tableBody.appendChild(card);
        });

        this.textContent = 'Le bouton a pas donovan';
    } else {
        location.reload();
    }
});