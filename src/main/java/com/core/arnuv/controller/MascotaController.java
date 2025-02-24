package com.core.arnuv.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.core.arnuv.request.PersonaDetalleRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IEnumOptionService;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.utils.ArnuvUtils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mascota")
@RequiredArgsConstructor
public class MascotaController {
	public final IMascotaDetalleService mscotaDetalleService;
	public final ICatalogoDetalleService catalogoDetalleService;
	public final IPersonaDetalleService personaDetalleService;
	private final IEnumOptionService enumOptionService;
	public final ArnuvUtils arnuvUtils;

	@GetMapping("/listar")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
	public String listar(Model model, HttpServletRequest request) {
		var idusuariologueado = arnuvUtils.getLoggedInUsername();
		if (request.isUserInRole("ADMIN")) {
			List<MascotaDetalle> listaMascotas = mscotaDetalleService.listarMascotasDetalle();
			model.addAttribute("lista", listaMascotas);
			return "content-page/mascotas-listar";
		}
		if (request.isUserInRole("CLIENTE") || request.isUserInRole("PASEADOR")) {
			List<MascotaDetalle> listaMascotas = mscotaDetalleService.findByIdpersonaId(idusuariologueado.getId());
			model.addAttribute("lista", listaMascotas);
			return "content-page/mascotas-listar";
		}
		return "redirect:/home";
	}

	@GetMapping("/nuevo")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
	public String crear(Model model, HttpServletRequest request) {
		model.addAttribute("nuevo", new MascotaDetalle());
		model.addAttribute("catalogo", catalogoDetalleService.listarCatalogoDetalle());
		model.addAttribute("comboTamano", enumOptionService.getTamanoPerroOptions());
		if (request.isUserInRole("ADMIN")) {
			Set<Personadetalle> personas = personaDetalleService.listarClientes();
			model.addAttribute("personas", personas);
		}
		return "content-page/mascotas-crear";
	}

	// guardar
	@PostMapping("/insertar")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
	public String guardar(@ModelAttribute("nuevo") MascotaDetalle nuevo, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {

			HttpSession session = request.getSession(false);
			PersonaDetalleRequest persona = (PersonaDetalleRequest) session.getAttribute("loggedInUser");
			MascotaDetalle mascota = nuevo;
			if (request.isUserInRole("CLIENTE")) {
				mascota.setIdpersona(personaDetalleService.buscarPorId(persona.getId()));
			}

			mascota.setPhotoPet(file);
			if (nuevo.getObservacion().length() == 0) {
				nuevo.setObservacion("");
			}
			mscotaDetalleService.insertarMascotaDetalle(nuevo);
			return "redirect:/mascota/listar";
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("message", "La imagen no se pudo guardar");
			return "content-page/mascotas-crear";
		}
	}

	// editar
	@GetMapping("/editar/{idmascota}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
	public String editar(@PathVariable(value = "idmascota") int codigo, Model model) {
		MascotaDetalle itemrecuperado = mscotaDetalleService.buscarMascotaID(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("catalogo", catalogoDetalleService.listarCatalogoDetalle());
		model.addAttribute("personas", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("comboTamano", enumOptionService.getTamanoPerroOptions());
		return "content-page/mascotas-crear";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model,
			RedirectAttributes redirectAttributes) {
		if (mscotaDetalleService.EliminarMascotaDetalle(codigo)) {
			redirectAttributes.addFlashAttribute("mensaje", "La mascota fué eliminada");
			redirectAttributes.addFlashAttribute("tipo", "success");
		} else {
			redirectAttributes.addFlashAttribute("mensaje",
					"Error la mascota no puede ser eliminada porque esta siendo usada o fué usada por algun paseo.");
			redirectAttributes.addFlashAttribute("tipo", "error");
		}
		return "redirect:/mascota/listar";
	}
}
