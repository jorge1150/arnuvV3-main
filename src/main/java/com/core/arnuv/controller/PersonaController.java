package com.core.arnuv.controller;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.RecordAcademico;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.IEnumOptionService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IRecordAcademicoService;
import com.core.arnuv.service.IRolService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import com.core.arnuv.services.imp.EmailSender;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import static com.core.arnuv.constants.Constants.*;

@Controller
@RequestMapping("/persona")
@RequiredArgsConstructor
public class PersonaController {
	private final IPersonaDetalleService servicioPersonaDetalle;
	private final IUbicacionService ubicacionService;
	private final IParametroService parametroService;
	private final IUsuarioDetalleService usuarioDetalleService;
	private final IUsuarioRolService usuarioRolService;
	private final IRolService rolService;
	private final EmailSender emailSender;
	private final IEnumOptionService enumOptionService;
	private final IRecordAcademicoService academicoService;

	@GetMapping("/listar")
	public String listarPersonas(@RequestParam(value = "rol", required = false, defaultValue = "") String rol,
			Model model) {
		Set<Personadetalle> listaPersonas;

		switch (rol) {
		case "ROLE_ADMIN":
			listaPersonas = servicioPersonaDetalle.listarAdministradores();
			break;
		case "ROLE_CLIENTE":
			listaPersonas = servicioPersonaDetalle.listarClientes();
			break;
		case "ROLE_PASEADOR":
			listaPersonas = servicioPersonaDetalle.listarPaseadores();
			break;
		default:
			listaPersonas = servicioPersonaDetalle.listarPersonas();
		}

		model.addAttribute("lista", listaPersonas);
		listarComboRol(model);
		return "content-page/persona-listar";
	}

	@GetMapping("crear")
	public String personCreate(Model model) {
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		model.addAttribute("errordata", null);
		return "content-page/persona-crear";
	}

	@PostMapping("create-access")
	private String personCreateAccess(@ModelAttribute("nuevo") PersonaDetalleRequest persona, Model model) {
		Personadetalle personadetalle = persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion",
				"iddetalleidentificacion");
		Personadetalle personaEntity;
		try {
			personadetalle.setFechaingreso(new Date());
			String error = servicioPersonaDetalle.verificarDuplicados(persona.getEmail(), persona.getCelular(),
					persona.getIdentificacion());
			if (!StringUtils.isEmpty(error)) {
				model.addAttribute("errordata", error);
				return "content-page/persona-crear";
			}
			personaEntity = servicioPersonaDetalle.insertarPersonaDetalle(personadetalle);
			var ubicacion = new Ubicacion();
			ubicacion.setLatitud(persona.getLatitud());
			ubicacion.setLongitud(persona.getLongitud());
			ubicacion.setIsDefault(1);
			ubicacion.setIdpersona(personaEntity);
			ubicacionService.insertarUbicacion(ubicacion);
			return "redirect:/usuario/crear/".concat(personaEntity.getId().toString());
		} catch (DataIntegrityViolationException e) {
			String errorMessage;
			errorMessage = "Error al guardar datos: Se ha detectado un problema con los datos ingresados.";
			model.addAttribute("errordata", errorMessage);
			model.addAttribute("nuevo", persona);
			return "content-page/persona-crear";
		}
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable(value = "id") int codigo, Model model) {
		Personadetalle itemrecuperado = servicioPersonaDetalle.buscarPorId(codigo);
		Usuariodetalle usuariodetalle = usuarioDetalleService.buscarpersona(codigo);
		Ubicacion ubicacion = ubicacionService.ubicacionPersonaPorDefecto(codigo);
		Usuariorol usuariorol = usuarioRolService.buscarIdUsuario(usuariodetalle.getIdusuario());
		PersonaDetalleRequest personaEntity = new PersonaDetalleRequest();

		personaEntity.setId(itemrecuperado.getId());
		personaEntity.setNombres(itemrecuperado.getNombres());
		personaEntity.setApellidos(itemrecuperado.getNombres());
		personaEntity.setEmail(itemrecuperado.getEmail());
		personaEntity.setCelular(itemrecuperado.getCelular());
		personaEntity.setIdentificacion(itemrecuperado.getIdentificacion());
		personaEntity.setLatitud(ubicacion.getLatitud());
		personaEntity.setLongitud(ubicacion.getLongitud());

		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaEntity);
		model.addAttribute("usuario", usuariodetalle);
		model.addAttribute("usuariorol", usuariorol);
		return "content-page/persona-ver";
	}

	@GetMapping("/perfil")
	public String perfil(Model model, HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		}
		Usuariodetalle usuariodetalleSession = usuarioDetalleService.buscarPorEmailOrUserName(username);
		Integer codigo = usuariodetalleSession.getIdpersona().getId();
		Personadetalle itemrecuperado = servicioPersonaDetalle.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		if (request.isUserInRole("PASEADOR")) {
			model.addAttribute("tipoPersona", "Paseador");
			model.addAttribute("comboEstado", enumOptionService.getEstadoAcademicoOptions());
			model.addAttribute("comboNivel", enumOptionService.getNivelAcademicoOptions());
			model.addAttribute("comboTamano", enumOptionService.getTamanoPerroOptions());
			model.addAttribute("recordsAcademicos", itemrecuperado.getRecordAcademico());
		}
		return "content-page/persona-perfil";
	}

	@PostMapping("actualizar-perfil")
	private String actualizarPerfil(@ModelAttribute("nuevo") Personadetalle personadetalle, Model model,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			Personadetalle personaBuscada = servicioPersonaDetalle.buscarPorId(personadetalle.getId());
			boolean banderaCorreo = !personaBuscada.getEmail().equals(personadetalle.getEmail());
			boolean banderaTelefono = !personaBuscada.getCelular().equals(personadetalle.getCelular());

			if (banderaCorreo) {
				String errorCorreo = servicioPersonaDetalle.verificarCorreo(personadetalle.getEmail());
				if (errorCorreo != null && !errorCorreo.trim().isEmpty()) {
					redirectAttributes.addFlashAttribute("errordata", errorCorreo);
					return "redirect:/persona/perfil";
				}
			}

			if (banderaTelefono) {
				String errorTelefono = servicioPersonaDetalle.verificarTelefono(personadetalle.getCelular());
				if (errorTelefono != null && !errorTelefono.trim().isEmpty()) {
					redirectAttributes.addFlashAttribute("errordata", errorTelefono);
					return "redirect:/persona/perfil";
				}
			}
			if (request.isUserInRole("PASEADOR")) {
				for (RecordAcademico recordAcademico : personadetalle.getRecordAcademico()) {
					if (recordAcademico.getId().equals(0)) {
						recordAcademico.setId(null);
						recordAcademico.setPersona(personadetalle);
						recordAcademico.setEstaEliminado(true);
					}
				}
			}
			personadetalle.setFechaingreso(personaBuscada.getFechaingreso());
			personadetalle.setFechamodificacion(new Date());
			servicioPersonaDetalle.insertarPersonaDetalle(personadetalle);
			Ubicacion ubicacionBuscada = ubicacionService.ubicacionPersonaPorDefecto(personadetalle.getId());
			ubicacionBuscada.setLatitud(personadetalle.getUbicaciones().get(0).getLatitud());
			ubicacionBuscada.setLongitud(personadetalle.getUbicaciones().get(0).getLongitud());
			ubicacionService.insertarUbicacion(ubicacionBuscada);
			if (request.isUserInRole("PASEADOR")) {
				for (RecordAcademico recordAcademico : personadetalle.getRecordAcademico()) {
					if (recordAcademico.getId() == null) {
						recordAcademico.setPersona(personadetalle);
						recordAcademico.setEstaEliminado(true);
						academicoService.guardarRecordAcademico(recordAcademico);
					}
				}
			}
			return "redirect:/home";
		} catch (DataIntegrityViolationException e) {
			String errorMessage;
			errorMessage = "Error al guardar datos: Se ha detectado un problema con los datos ingresados.";
			model.addAttribute("errordata", errorMessage);
			model.addAttribute("nuevo", personadetalle);
			return "content-page/persona-perfil";
		}
	}

	@PostMapping("/inavilitaUsuario")
	private String inavilitaUsuario(@ModelAttribute("nuevo") Personadetalle nuevo)
			throws UnsupportedEncodingException, MessagingException {
		Personadetalle personadetalle = servicioPersonaDetalle.buscarPorIdentificacion(nuevo.getIdentificacion());
		Usuariodetalle usuariodetalle = usuarioDetalleService.buscarpersona(personadetalle.getId());

		Boolean estado = false;
		if (usuariodetalle.getEstado() == false) {
			estado = true;
		}

		if (Boolean.TRUE.equals(usuariodetalle.getEstado())) {
			estado = false;
		}
		usuariodetalle.setEstado(estado);
		usuarioDetalleService.actualizarUsuarioDetalle(usuariodetalle);

		String htmlContent = new String(parametroService.getParametro(KEY_MAIL_ESTADO).getArchivos(),
				StandardCharsets.UTF_8);
		htmlContent = htmlContent.replace("{{estado}}", estado ? "Activo" : "Inactivo");

		emailSender.sendEmail(personadetalle.getEmail(), "ACTIVACIÓN DE CUENTA", htmlContent);

		return "redirect:/persona/listar";
	}

	private void listarComboRol(Model model) {
		model.addAttribute("listaRoles", rolService.listarTodosRoles());
	}
}