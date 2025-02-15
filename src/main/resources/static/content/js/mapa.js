document.addEventListener("DOMContentLoaded", function () {
    if (window.paseadoresData) {
        inicializarMapa(window.paseadoresData);
    } else {
        console.warn("No hay datos de paseadores.");
    }
});

function inicializarMapa(paseadores) {
    var map = L.map('map').setView([-0.244879, -78.542727], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    var markers = L.markerClusterGroup();

    paseadores.forEach(paseador => {
        if (paseador.latitud && paseador.longitud) {
            paseador.nombre = paseador.nombre.replace(/"/g, '').trim();
            var marker = L.marker([parseFloat(paseador.latitud), parseFloat(paseador.longitud)])
                .bindPopup("🚶 Paseador: " + paseador.nombre)
                .on('click', function () {
                    document.getElementById("personaSelect").value = paseador.codigo;
                });

            markers.addLayer(marker);
        }
    });

    map.addLayer(markers);
}
