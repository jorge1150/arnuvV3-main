document.addEventListener("DOMContentLoaded", function () {
  if (window.ubicacionDefault) {
    inicializarMapa(
      window.ubicacionDefault,
      window.ubicacionCliente,
      window.vistaMapa
    );
  } else {
    console.warn("No hay datos de paseadores.");
  }
});

function inicializarMapa(ubicacionDefault, ubicacionCliente, vistaMapa) {
  var map = L.map("map").setView(
    [ubicacionDefault.latitud, ubicacionDefault.longitud],
    vistaMapa
  );

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors",
  }).addTo(map);

  var markers = L.markerClusterGroup();

  // Definir iconos personalizados
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

  L.Routing.control({
    waypoints: [
      L.latLng(ubicacionDefault.latitud, ubicacionDefault.longitud), // Paseador
      L.latLng(ubicacionCliente.latitud, ubicacionCliente.longitud), // Cliente
    ],
    routeWhileDragging: true,
    createMarker: function (i, waypoint, n) {
      let icon = i === 0 ? iconoPaseador : iconoCliente; // Primer punto = paseador, segundo = cliente
      return L.marker(waypoint.latLng, { icon: icon }).bindPopup(
        i === 0 ? "🚶 Paseador" : "🐶 Cliente"
      );
    },
    router: L.Routing.osrmv1({
      language: "es", // Configura el idioma de las instrucciones
      profile: "foot", // Puedes cambiar a 'car', 'bike', etc.
    }),
  }).addTo(map);

  setTimeout(() => {
    let routingContainer = document.querySelector(".leaflet-routing-container");
    if (routingContainer) {
      routingContainer.style.backgroundColor = "#2c3e50"; // Fondo oscuro
      routingContainer.style.color = "white"; // Texto blanco
      routingContainer.style.borderRadius = "8px";
      routingContainer.style.padding = "10px";
      routingContainer.style.maxHeight = "200px"; // Altura máxima antes de aparecer el scroll
      routingContainer.style.overflowY = "auto";
    }
  }, 1000);
}
