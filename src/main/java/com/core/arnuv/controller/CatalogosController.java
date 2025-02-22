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

import com.core.arnuv.model.CatalogoDetalle;
import com.core.arnuv.service.ICatalogoDetalleService;

@Controller
@RequestMapping("/catalogo")
@RequiredArgsConstructor
public class CatalogosController {
	public final ICatalogoDetalleService catalogoDetalleService;

	@GetMapping("/listar")
	public String listarColores(Model model) {
		List<CatalogoDetalle> listacatalogos = catalogoDetalleService.listarCatalogoDetalle();
		model.addAttribute("lista", listacatalogos);
		return "content-page/catalogo-listar";
	}

	@GetMapping("/nuevo")
	public String crear(Model model) {
		model.addAttribute("nuevo", new CatalogoDetalle());
		return "content-page/catalogo-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") CatalogoDetalle nuevo) {
		nuevo.setActivo(true);
		catalogoDetalleService.insertarCatalogoDetalle(nuevo);
		return "redirect:/catalogo/listar";
	}

	@GetMapping("/editar/{idcatalogo}")
	public String editarCurso(@PathVariable(value = "idcatalogo") int codigo, Model model) {
		CatalogoDetalle itemrecuperado = catalogoDetalleService.buscarCatalogoDetalleId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		return "content-page/catalogo-crear";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminarCatalogo(@PathVariable(value = "codigo") int codigo, RedirectAttributes redirectAttributes) {
	    try {
	        if(catalogoDetalleService.eliminarCatalogoDetalle(codigo)) {
	        	redirectAttributes.addFlashAttribute("mensaje", "La raza fué eliminada");
		        redirectAttributes.addFlashAttribute("tipo", "success");
	        }else {
	        	 redirectAttributes.addFlashAttribute("mensaje", "Error la raza no puede ser eliminada porque esta siendo usada por las mascotas.");
	 	        redirectAttributes.addFlashAttribute("tipo", "error");
	        }
	        
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("mensaje", "Error la raza no puede ser eliminada porque esta siendo usada por las mascotas.");
	        redirectAttributes.addFlashAttribute("tipo", "error");
	    }
	    return "redirect:/catalogo/listar";
	}

}
