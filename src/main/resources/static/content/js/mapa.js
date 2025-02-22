document.addEventListener("DOMContentLoaded", function () {
  if (window.paseadoresData) {
    inicializarMapa(
      window.paseadoresData,
      window.ubicacionDefault,
      window.vistaMapa
    );
  } else {
    console.warn("No hay datos de paseadores.");
  }
});

function inicializarMapa(paseadores, ubicacionDefault, vistaMapa) {
  var map = L.map("map").setView(
    [ubicacionDefault.latitud, ubicacionDefault.longitud],
    vistaMapa
  );

  var iconoPaseador = L.icon({
    iconUrl: "https://cdn-icons-png.flaticon.com/128/236/236832.png",
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32],
  });

  var iconoCliente = L.icon({
    iconUrl: "https://cdn-icons-png.flaticon.com/128/616/616408.png", // Perro
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32],
  });

  var markerCliente = L.marker(
    [
      parseFloat(ubicacionDefault.latitud),
      parseFloat(ubicacionDefault.longitud),
    ],
    { icon: iconoCliente }
  ).bindPopup("🐶 Mi ubicación");

  map.addLayer(markerCliente);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors",
  }).addTo(map);

  var markers = L.markerClusterGroup();

  paseadores.forEach((paseador) => {
    if (paseador.latitud && paseador.longitud) {
      paseador.nombre = paseador.nombre.replace(/"/g, "").trim();
      paseador.preferencias = paseador.preferencias
        .replace(/["\[\]]/g, "")
        .trim();
      var popupContent = `
            <div style="font-size: 12px; text-align: center;">
                <strong>🚶 ${paseador.nombre}</strong><br>
                Preferencias: ${paseador.preferencias}  <br>  Años de experiencia: ${paseador.experiencia}<br><br>
                <button class="solicitar-btn" data-codigo="${paseador.codigo}" 
                        style="background: #007bff; color: white; border: none; padding: 3px 7px; font-size: 12px; cursor: pointer;">
                    Solicitar
                </button>
            </div>
        `;

      var marker = L.marker(
        [parseFloat(paseador.latitud), parseFloat(paseador.longitud)],
        { icon: iconoPaseador }
      ).bindPopup(popupContent);

      markers.addLayer(marker);
    }
  });

  map.addLayer(markers);
}

// Evento global para capturar clics en los botones dentro del popup
document.addEventListener("click", function (event) {
  if (event.target.classList.contains("solicitar-btn")) {
    let codigo = event.target.getAttribute("data-codigo");
    document.getElementById("personaSelect").value = codigo;
  }
});
