package com.core.arnuv.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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
	public String eliminarColor(@PathVariable(value = "codigo") int codigo, Model model) {
		catalogoDetalleService.eliminarCatalogoDetalle(codigo);
		return "redirect:/catalogo/listar";
	}
}
