	//valida que no ingresen  puntos ni comas en el formulario
	function validarNombre(input) {
        // Expresión regular para caracteres permitidos: letras, números y espacios
        const regex = /^[A-Za-z0-9\s]*$/;
        // Reemplaza los caracteres no permitidos con una cadena vacía
        input.value = input.value.replace(/[^A-Za-z0-9\s]/g, '');
    }

