package com.core.arnuv.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.service.IPersonaDetalleService;

@Controller
public class IndexController {
	@Autowired
	IPersonaDetalleService personaDetalleService;

	@GetMapping({ "/", "/index" })
	public String index(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			return "redirect:/auth/default";
		}
		return "landing/index";
	}

	@GetMapping("/home")
	public String home() {
		return "content-page/blank";
	}

	@GetMapping("/prueba2")
	public String prueba2(Model model) {
		List<Personadetalle> listPersona = personaDetalleService.listarTodosPersonaDetalle();
		List<Ubicacion> ubicaciones = new ArrayList<>();
		for (Personadetalle persona : listPersona) {
			for (Ubicacion ubicacion : persona.getUbicaciones()) {
				ubicaciones.add(ubicacion);
			}
			
		}
		model.addAttribute("listaPaseadores", ubicaciones);
		return "content-page/prueba2";
	}

}