 let isRunning = true;  // Estado inicial: el perro está corriendo

  document.getElementById('runningDog').addEventListener('click', function() {
      if (isRunning) {
          // Pausa la animación del perro corriendo y muestra el perro moviendo la cola
          this.style.animationPlayState = 'paused';
          this.style.display = 'none';
          document.getElementById('waggingDog').style.display = 'block';
      } else {
          // Reinicia la animación del perro corriendo desde el principio
          this.style.animation = 'none';  // Elimina la animación
          this.offsetHeight;  // Dispara un reflujo para reiniciar la animación
          this.style.animation = '';  // Vuelve a aplicar la animación original
          this.style.animationPlayState = 'running';  // Reanuda la animación
          this.style.display = 'block';
          document.getElementById('waggingDog').style.display = 'none';
      }
      isRunning = !isRunning;  // Cambia el estado
  });

  document.getElementById('waggingDog').addEventListener('click', function() {
      // Reinicia la animación del perro corriendo y oculta el perro moviendo la cola
      document.getElementById('runningDog').style.animation = 'none';  // Elimina la animación
      document.getElementById('runningDog').offsetHeight;  // Dispara un reflujo para reiniciar la animación
      document.getElementById('runningDog').style.animation = '';  // Vuelve a aplicar la animación original
      document.getElementById('runningDog').style.animationPlayState = 'running';  // Reanuda la animación
      document.getElementById('runningDog').style.display = 'block';
      this.style.display = 'none';
      isRunning = true;  // Resetea el estado a corriendo
  });