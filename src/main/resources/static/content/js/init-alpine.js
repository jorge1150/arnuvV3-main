function data() {
	function getThemeFromLocalStorage() {
		// if user already changed the theme, use it
		if (window.localStorage.getItem('dark')) {
			return JSON.parse(window.localStorage.getItem('dark'))
		}

		// else return their preferences
		return (
			!!window.matchMedia &&
			window.matchMedia('(prefers-color-scheme: dark)').matches
		)
	}

	function setThemeToLocalStorage(value) {
		window.localStorage.setItem('dark', value)
	}

	return {
		dark: getThemeFromLocalStorage(),
		toggleTheme() {
			this.dark = !this.dark
			setThemeToLocalStorage(this.dark)
		},
		isSideMenuOpen: false,
		toggleSideMenu() {
			this.isSideMenuOpen = !this.isSideMenuOpen
		},
		closeSideMenu() {
			this.isSideMenuOpen = false
		},
		isNotificationsMenuOpen: false,
		toggleNotificationsMenu() {
			this.isNotificationsMenuOpen = !this.isNotificationsMenuOpen
		},
		closeNotificationsMenu() {
			this.isNotificationsMenuOpen = false
		},
		isProfileMenuOpen: false,
		toggleProfileMenu() {
			this.isProfileMenuOpen = !this.isProfileMenuOpen
		},
		closeProfileMenu() {
			this.isProfileMenuOpen = false
		},
		isPagesMenuOpen: false,
		togglePagesMenu() {
			this.isPagesMenuOpen = !this.isPagesMenuOpen
		},
		// Modal
		isModalOpen: false,
		trapCleanup: null,
		openModal() {
			this.isModalOpen = true
			this.trapCleanup = focusTrap(document.querySelector('#modal'))
		},
		closeModal() {
			this.isModalOpen = false
			this.trapCleanup()
		},
	}
}

//---------------------------------------------------------------------------------------------------
function initMap1() {
	var map = new google.maps.Map(document
		.getElementById('map'), {
		zoom: 15
	});

	// Intentar geolocalizar al usuario
	if (navigator.geolocation) {
		navigator.geolocation
			.getCurrentPosition(
				function(position) {
					var pos = {
						lat: position.coords.latitude,
						lng: position.coords.longitude
					};

					// Centrar el mapa en la ubicación del usuario
					map.setCenter(pos);

					// Colocar un marcador en la ubicación del usuario
					var marker = new google.maps.Marker(
						{
							position: pos,
							map: map,
							title: 'Tu ubicación'
						});

					// Llenar los campos de latitud y longitud
					document
						.getElementById('latitud').value = pos.lat;
					document
						.getElementById('longitud').value = pos.lng;

					var latlng = { lat: pos.lat, lng: pos.lng };

					// Geocoder
					var geocoder = new google.maps.Geocoder();
					geocodeLatLng(geocoder, latlng);

				}, function() {
					handleLocationError(true,
						map.getCenter());
				});
	} else {
		// El navegador no soporta geolocalización
		handleLocationError(false, map.getCenter());
	}

	// Añadir marcadores para cada ubicación desde el servidor
	var locations = /*[[${locations}]]*/[];
	locations.forEach(function(location) {
		var marker = new google.maps.Marker({
			position: {
				lat: location.latitude,
				lng: location.longitude
			},
			map: map,
			title: location.description
		});
	});

	// Capturar el clic en el mapa
	map
		.addListener(
			'click',
			function(event) {
				document
					.getElementById('latitud').value = event.latLng.lat();
				document
					.getElementById('longitud').value = event.latLng
						.lng();

				var latlng = { lat: event.latLng.lat(), lng: event.latLng.lng() };



				// Geocoder
				var geocoder = new google.maps.Geocoder();
				geocodeLatLng(geocoder, latlng);

				// Mover el marcador a la nueva ubicación
				console.log("aqui javi");
				var position = {
					lat: event.latLng.lat(),
					lng: event.latLng.lng()

				};
				new google.maps.Marker({
					position: position,
					map: map,
					title: categoria.nombre
				});
				/*marker
					.setPosition(event.latLng);
				map.setCenter(event.latLng);
				*/
			});
}

//---------------------------------------------------------------------------------------------------



function initMap() {
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom: 15,
		center: { lat: -34.397, lng: 150.644 }
	});

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat: position.coords.latitude,
				lng: position.coords.longitude
			};

			currentMarker = new google.maps.Marker({
				position: pos,
				map: map
			});

			map.setCenter(pos);
			updateLatLongFields(pos.lat, pos.lng);

			var latlng = { lat: pos.lat, lng: pos.lng };
			// Geocoder
			var geocoder = new google.maps.Geocoder();
			geocodeLatLng(geocoder, latlng);

		}, function() {
			handleLocationError(true, map.getCenter());
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false, map.getCenter());
	}
	//debugger;
	// Add click event listener to the map
	map.addListener('click', function(event) {
		placeMarker(event.latLng, map);
	});

}

function placeMarker(location, map) {
	if (currentMarker) {
		currentMarker.setMap(null); // Remove the previous marker
	}
	currentMarker = new google.maps.Marker({
		position: location,
		map: map
	});
	map.panTo(location);
	updateLatLongFields(location.lat(), location.lng());
	var latlng = { lat: location.lat(), lng: location.lng() };
	// Geocoder
	var geocoder = new google.maps.Geocoder();
	geocodeLatLng(geocoder, latlng);
}

function updateLatLongFields(lat, lng) {
	document.getElementById('latitud').value = lat;
	document.getElementById('longitud').value = lng;
}

function handleLocationError(browserHasGeolocation, pos) {
	var infoWindow = new google.maps.InfoWindow({
		position: pos,
		content: browserHasGeolocation ?
			'Error: The Geolocation service failed.' :
			'Error: Your browser doesn\'t support geolocation.'
	});
	infoWindow.open(map);
}
////////////----------------------------------------------------------------------------

function handleLocationError(browserHasGeolocation, pos) {
	var infoWindow = new google.maps.InfoWindow({
		map: map
	});
	infoWindow.setPosition(pos);
	infoWindow
		.setContent(browserHasGeolocation ? 'Error: El servicio de geolocalización falló.'
			: 'Error: Tu navegador no soporta geolocalización.');
}

function showPoint() {
	var lat = parseFloat(document
		.getElementById('latitud').value);
	var lng = parseFloat(document
		.getElementById('longitud').value);
	var pos = {
		lat: lat,
		lng: lng
	};

	// Mover el marcador a la ubicación ingresada
	if (marker) {
		marker.setPosition(pos);
	} else {
		marker = new google.maps.Marker({
			position: pos,
			map: map
		});
	}
	map.setCenter(pos);
}


function geocodeLatLng(geocoder, latlng) {
	geocoder.geocode({ 'location': latlng }, function(results, status) {
		if (status === 'OK') {
			if (results[0]) {
				console.log(results[0]);

				// Dirección Formateada
				document.getElementById('Sector').value = results[0].formatted_address; //results[0].address_components[0].long_name;



			} else {
				console.log('No se encontraron resultados');
			}
		} else {
			console.log('Geocoder falló debido a: ' + status);
		}
	});
}


function makeSelectReadonly(selectId) {
	const selectElement = document.getElementById(selectId);

	if (!selectElement) {
		console.error(`Element with id ${selectId} not found.`);
		return;
	}

	selectElement.classList.add('readonly-select');

	selectElement.addEventListener('mousedown', function(e) {
		e.preventDefault();
	});

	selectElement.addEventListener('keydown', function(e) {
		e.preventDefault();
	});
}


function initDataTable() {
	$('#dataTable').DataTable({
		responsive: true,
		autoWidth: false, // Asegura que la tabla no se expanda fuera de su contenedor
		language: {
			url: '//cdn.datatables.net/plug-ins/1.11.5/i18n/es-ES.json'
		},
		dom: '<"top"f>rt<"bottom"lp><"clear">', // Para que la barra de búsqueda sea también responsiva
		drawCallback: function() {
			// Asegúrate de que las líneas divisorias estén presentes después de cada actualización
			$('#dataTable thead th, #dataTable tbody td').css({
				'border': '1px solid #e5e7eb'
			});
		}
	});
}


function handleResize() {
	const table = document.getElementById('dataTable');
	const isLargeScreen = window.innerWidth >= 1024; // 1024px es el breakpoint para pantallas grandes en Tailwind

	if (isLargeScreen) {
		// Mostrar columna en pantallas grandes
		table.querySelectorAll('.estado-column').forEach(cell => {
			cell.classList.remove('hidden');
			cell.classList.add('lg:table-cell');
		});
	} else {
		// Ocultar columna en pantallas pequeñas
		table.querySelectorAll('.estado-column').forEach(cell => {
			cell.classList.remove('lg:table-cell');
			cell.classList.add('hidden');
		});
	}
}

// Función para alternar la visibilidad de la columna con el botón
function toggleColumnVisibility() {
	const table = document.getElementById('dataTable');
	const columns = table.querySelectorAll('.estado-column');

	columns.forEach(cell => {
		if (cell.classList.contains('hidden')) {
			cell.classList.remove('hidden');
			cell.classList.add('lg:table-cell');
		} else {
			cell.classList.remove('lg:table-cell');
			cell.classList.add('hidden');
		}
	});
}

//funcion para validar que la fecha no sea menor a al actual
function handleDateChange(input) {
	const selectedDate = new Date(input.value);
	const currentDate = new Date();

	if (selectedDate < currentDate) {
		alert("La fecha seleccionada no puede ser anterior a la fecha y hora actuales.");
		// Restablece el valor al mínimo permitido
		input.value = input.min;
	} else {
		console.log("Fecha seleccionada: ", input.value);
		// Aquí puedes añadir la lógica adicional que necesites
	}
}

//mouestra las observsacion de las tarifas en el caso de tener 
function mostrarObservacionesLabel() {
	var select = document.getElementById("opcionesTarifa");
	var label = document.getElementById("resultadoObservacion");
	var selectedOption = select.options[select.selectedIndex];
	// Obtener el valor seleccionado
	var seleccion = selectedOption.getAttribute("data-observacion");

	if (!seleccion) {

		label.textContent = "";

	} else {

		label.textContent = "Observación de tarifa: " + seleccion;
	}

}