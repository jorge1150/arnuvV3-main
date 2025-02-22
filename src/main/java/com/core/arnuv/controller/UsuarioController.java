package com.core.arnuv.controller;

import com.core.arnuv.enums.RolEnum;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.model.UsuariorolId;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IRolService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import com.core.arnuv.utils.ArnuvNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
	private final IPersonaDetalleService servicioPersonaDetalle;
	private final IUsuarioDetalleService usuarioDetalleService;
	private final PasswordEncoder passwordEncoder;
	private final IRolService servicioRol;
	private final IUsuarioRolService servicioUsuarioRol;

	@GetMapping("crear/{personaId}")
	public String personCreate(Model model, @PathVariable("personaId") Integer personaId) {
		UsuarioDetalleRequest requestUser = new UsuarioDetalleRequest();
		requestUser.setIdpersona(personaId);
		model.addAttribute("nuevo", requestUser);
		return "content-page/usuario-crear";
	}

	@PostMapping("create-access")
	private String personCreateAccess(@ModelAttribute("nuevo") UsuarioDetalleRequest usuario, Model model) {
		var personaentity = servicioPersonaDetalle.buscarPorId(usuario.getIdpersona());
		Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
		if (usuarioDetalleService.buscarPorEmailOrUserName(usuario.getUsername()) != null) {
			model.addAttribute("nuevo", usuario);
			model.addAttribute("errorUsuario", usuario);
			return "content-page/usuario-crear";
		}
		usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuariodetalle.setIdpersona(personaentity);
		usuariodetalle.setEstado(true);
		try {
			Usuariodetalle usuarioEnt = usuarioDetalleService.insertarUsuarioDetalle(usuariodetalle);
			var rolentity = servicioRol.findByNombre(RolEnum.ROLE_ADMIN.getValue());
			UsuariorolId usuariorolId = new UsuariorolId();
			usuariorolId.setIdusuario(usuarioEnt.getIdusuario());
			usuariorolId.setIdrol(rolentity.getId());
			Usuariorol usuariorol = new Usuariorol();
			usuariorol.setIdrol(rolentity);
			usuariorol.setIdusuario(usuarioEnt);
			usuariorol.setId(usuariorolId);
			servicioUsuarioRol.insertarUsuarioRol(usuariorol);
		} catch (DataIntegrityViolationException e) {
			throw new ArnuvNotFoundException("Error al guardar datos: {0}",
					e.getMessage().split("Detail:")[1].split("]")[0]);
		}
		return "redirect:/persona/listar";
	}
}