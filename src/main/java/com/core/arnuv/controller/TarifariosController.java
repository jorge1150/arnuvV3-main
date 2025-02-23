package com.core.arnuv.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.arnuv.model.Tarifario;
import com.core.arnuv.service.ITarifarioService;

@Controller
@RequestMapping("/tarifario")
@RequiredArgsConstructor
public class TarifariosController {
	public final ITarifarioService tarifarioService;

	@GetMapping("/listar")
	public String listarTarifario(Model model) {
		List<Tarifario> listacatalogos = tarifarioService.listarTarifarios();
		model.addAttribute("lista", listacatalogos);
		return "content-page/tarifario-listar";
	}

	@GetMapping("/nuevo")
	public String crearTarifario(Model model) {
		model.addAttribute("nuevo", new Tarifario());
		return "content-page/tarifario-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardarTarifario(@ModelAttribute("nuevo") Tarifario nuevo) {
		List<Tarifario> tarifas = tarifarioService.listarTarifarios();
		for (Tarifario tarifario : tarifas) {
			if (tarifario.getActivo()) {
				tarifario.setActivo(false);
				tarifarioService.insertarTarifario(tarifario);
			}
		}
		nuevo.setActivo(true);
		tarifarioService.insertarTarifario(nuevo);
		return "redirect:/tarifario/listar";
	}

	@GetMapping("/editar/{idcatalogo}")
	public String editarTarifario(@PathVariable(value = "idcatalogo") int codigo, Model model) {
		Tarifario itemrecuperado = tarifarioService.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		return "content-page/tarifario-crear";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminarTarifario(@PathVariable(value = "codigo") int codigo, RedirectAttributes redirectAttributes) {
		try {
			Tarifario busqueda = tarifarioService.buscarPorId(codigo);
			if (busqueda.getActivo()) {
				redirectAttributes.addFlashAttribute("mensaje", "Error, no se puede eliminar una tarifa activa.");
				redirectAttributes.addFlashAttribute("tipo", "error");
				return "redirect:/tarifario/listar";
			}
			if (tarifarioService.eliminarTarifario(codigo)) {
				redirectAttributes.addFlashAttribute("mensaje", "La tarifa fué eliminada");
				redirectAttributes.addFlashAttribute("tipo", "success");
			} else {
				redirectAttributes.addFlashAttribute("mensaje",
						"Error la tarifa no puede ser eliminada porque esta siendo usada por algún paseo.");
				redirectAttributes.addFlashAttribute("tipo", "error");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("mensaje",
					"Error la tarifa no puede ser eliminada porque esta siendo usada por algún paseo.");
			redirectAttributes.addFlashAttribute("tipo", "error");
		}
		return "redirect:/tarifario/listar";
	}
}
