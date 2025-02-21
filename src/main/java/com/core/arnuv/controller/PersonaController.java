package com.core.arnuv.controller;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IRolService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import com.core.arnuv.services.imp.EmailSender;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

	@PostMapping("/inavilitaUsuario")
	private String inavilitaUsuario(@ModelAttribute("nuevo") Personadetalle nuevo)
			throws UnsupportedEncodingException, MessagingException {
		Personadetalle personadetalle = servicioPersonaDetalle.buscarPorIdentificacion(nuevo.getIdentificacion());
		Usuariodetalle usuariodetalle = usuarioDetalleService.buscarpersona(personadetalle.getId());

		String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(),
				StandardCharsets.UTF_8);

		String fechaEspanol = Strings.EMPTY;
		String mensajeDinamico = "";

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
		String formattedDate = formatter.format(date);
		fechaEspanol = formattedDate;

		Boolean estado = null;
		if (usuariodetalle.getEstado() == null) {
			estado = true;
			mensajeDinamico = "SU USUARIO A SIDO HABILITADO" + " FECHA:" + fechaEspanol
					+ ", PUEDES VOLVER INGRESAR AL SISTEMA ARNUV !!";
		}

		if (Boolean.TRUE.equals(usuariodetalle.getEstado())) {
			estado = null;
			mensajeDinamico = "SU  USUARIO A SIDO DESHABILITADO" + " FECHA:" + fechaEspanol
					+ ", CONTACTATE CON EL ADMINSTRADOR DEL SISTEMA ARNUV !!";

		}
		usuariodetalle.setEstado(estado);
		usuarioDetalleService.actualizarUsuarioDetalle(usuariodetalle);

		htmlContent = htmlContent.replace("{{mensajeBienvenida}}",
				"<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">"
						+ mensajeDinamico.toUpperCase() + "</span></p>");
		emailSender.sendEmail(personadetalle.getEmail(), "FUNDACIÓN ARNUV", htmlContent);

		return "redirect:/persona/listar";
	}

	private void listarComboRol(Model model) {
		model.addAttribute("listaRoles", rolService.listarTodosRoles());
	}
}