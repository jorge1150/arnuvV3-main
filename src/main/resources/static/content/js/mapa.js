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

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors",
  }).addTo(map);

  var markers = L.markerClusterGroup();

  paseadores.forEach((paseador) => {
    if (paseador.latitud && paseador.longitud) {
      paseador.nombre = paseador.nombre.replace(/"/g, "").trim();
      var marker = L.marker([
        parseFloat(paseador.latitud),
        parseFloat(paseador.longitud),
      ])
        .bindPopup("🚶 Paseador: " + paseador.nombre)
        .on("click", function () {
          document.getElementById("personaSelect").value = paseador.codigo;
        });

      markers.addLayer(marker);
    }
  });

  map.addLayer(markers);
}
