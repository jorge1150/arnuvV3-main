package com.core.arnuv;

import com.core.arnuv.repository.IParametroRepository;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.utils.ArnuvUtils;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ArnuvApplicationTests {

	@Autowired
	private IParametroService service;
	@Test
	void contextLoads() {
	}

	@Data
	public class Usuario {
		private String nombre;
		private double latitud;
		private double longitud;
		public Usuario(String nombre, double latitud, double longitud) {
			this.nombre = nombre;
			this.latitud = latitud;
			this.longitud = longitud;
		}
		}

}
