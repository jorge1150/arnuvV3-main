package com.core.arnuv.controller;


import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.core.arnuv.model.Calificacion;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.service.ICalificacionService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.utils.ArnuvUtils;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/calificacion")
@RequiredArgsConstructor
public class CalificacionController {
	private final ICalificacionService calificacionService;
	private final IPaseoService paseoService;
	private final IPersonaDetalleService personaDetalleService;
	private final ArnuvUtils arnuvUtils;
	
	@GetMapping("/listar")
	public String listar(Model model, HttpServletRequest request) {
		var idusuariologueado =arnuvUtils.getLoggedInUsername();		
		if (request.isUserInRole("ADMIN")) {
			List<Calificacion> listaCalificacion = calificacionService.listarCalificacion();
			model.addAttribute("lista", listaCalificacion);
			return "content-page/calificacion-listar";
		}
		if (request.isUserInRole("PASEADOR")) {
			List<Calificacion> listaCalificacion = calificacionService.BuscarPersonaPasedor(idusuariologueado.getId());
			model.addAttribute("lista", listaCalificacion);
			return "content-page/calificacion-listar";
		}
		return "redirect:/home";
		

	}
	
	@GetMapping("/nuevo/{paseoId}")
	public String crear(Model model, @PathVariable(value = "paseoId") int paseoId) {	

		System.out.print(paseoId);
		Calificacion calificacion = new Calificacion();
		calificacion.setPaseoID(paseoId);
		model.addAttribute("nuevo", calificacion);		
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());		
		return "content-page/calificacion-crear";
	}
	
	@PostMapping("/insertar")
	public String insertarCalificacion(@ModelAttribute("nuevaCalificacion") Calificacion nuevo,Model model)  {
		try {
			nuevo.setFecha(new Date());
			Paseo paseo = paseoService.buscarPorId(nuevo.getPaseoID());
			nuevo.setIdpaseo(paseo);
			calificacionService.insertarCalificacion(nuevo);
			return "redirect:/paseo/editarCliente/".concat(String.valueOf(nuevo.getPaseoID()));
		} catch ( DataIntegrityViolationException e) {
			String errorMessage = "El paseador: "+ nuevo.getIdpaseo().getIdpersonapasedor().getNombres()+" "+nuevo.getIdpaseo().getIdpersonapasedor().getApellidos()+ " ya fue calificado" ;
			model.addAttribute("error", errorMessage);
			model.addAttribute("nuevo", nuevo);
			return "content-page/Calificacion-crear";
		}
	}
}
