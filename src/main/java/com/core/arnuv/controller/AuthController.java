package com.core.arnuv.controller;

import com.core.arnuv.constants.Constants;
import com.core.arnuv.model.*;
import com.core.arnuv.request.ChangePasswordRequest;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.RecordAcademicoRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.IEnumOptionService;
import com.core.arnuv.service.IMenuService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.services.imp.EmailSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.core.arnuv.constants.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
	private final IUsuarioDetalleService userService;
	private final IMenuService menuService;
	private final IPersonaDetalleService servicioPersonaDetalle;
	private final PasswordEncoder passwordEncoder;
	private final EmailSender emailSender;
	private final IParametroService parametroService;
	private final IEnumOptionService enumOptionService;
	ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			return "redirect:/auth/default";
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			AuthenticationException exception = (AuthenticationException) session
					.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			if (exception != null) {
				model.addAttribute("errorMessage", exception.getMessage());
				// Limpia la excepción después de manejarla
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}
		return "landing/login";
	}

	/*-----------------------CREAR NUEVO CLIENTE ------------------------*/
	@GetMapping("/crearCliente/{tipoPersona}")
	public String personCliente(@PathVariable("tipoPersona") String tipoPersona, Model model) {
		if (Constants.PASEADOR.equals(tipoPersona)) {
			model.addAttribute("comboEstado", enumOptionService.getEstadoAcademicoOptions());
			model.addAttribute("comboNivel", enumOptionService.getNivelAcademicoOptions());
			model.addAttribute("comboTamano", enumOptionService.getTamanoPerroOptions());
		}
		PersonaDetalleRequest nuevo = new PersonaDetalleRequest();
		nuevo.setTipoPersona(tipoPersona);
		model.addAttribute("tipoPersona", tipoPersona);
		model.addAttribute("nuevo", nuevo);
		return "landing/persona-crear-cliente";
	}

	@PostMapping("/create-access")
	private String personCreateAccess(@ModelAttribute("nuevo") PersonaDetalleRequest persona, Model model,
			RedirectAttributes redirectAttributes, @RequestParam("recordsAcademicos") String recordsAcademicosJson) {
		try {
			String error = servicioPersonaDetalle.verificarDuplicados(persona.getEmail(), persona.getCelular(),
					persona.getIdentificacion());
			if (!StringUtils.isEmpty(error)) {
				redirectAttributes.addFlashAttribute("error", error);
				if (Constants.PASEADOR.equals(persona.getTipoPersona())) {
					return "redirect:/auth/crearCliente/Paseador";
				}
				return "redirect:/auth/crearCliente/Cliente";
			}

			if (Constants.PASEADOR.equals(persona.getTipoPersona())) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<RecordAcademicoRequest> recordsAcademicos = null;
				recordsAcademicos = objectMapper.readValue(recordsAcademicosJson,
						new TypeReference<List<RecordAcademicoRequest>>() {
						});
				persona.setRecordsAcademicosList(recordsAcademicos);
			} else {
				persona.setRecordsAcademicosList(new ArrayList<>());
			}
			UsuarioDetalleRequest requestUser = new UsuarioDetalleRequest();
			requestUser.setPersona(objectMapper.writeValueAsString(persona));
			model.addAttribute("nuevo", requestUser);
			return "landing/usuario-crear-cliente";
		} catch (Exception e) {
			String errorMessage = "Error al guardar datos: Se ha detectado un problema con los datos ingresados.";
			model.addAttribute("error", errorMessage);
			model.addAttribute("nuevo", persona);
			return "landing/persona-crear-cliente";
		}
	}

	@PostMapping("/createAccessUsuarioCliente")
	private String personCreateAccess(@ModelAttribute("nuevo") UsuarioDetalleRequest usuario, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			PersonaDetalleRequest persona = objectMapper.readValue(usuario.getPersona(), PersonaDetalleRequest.class);
			List<RecordAcademicoRequest> listRecord = persona.getRecordsAcademicosList();
			var personaentity = servicioPersonaDetalle.guardarInformacionCompleta(persona, usuario, listRecord);

			Usuariodetalle usuarioEnt = userService.buscarPorEmailOrUserName(personaentity.getEmail());
			if (Constants.PASEADOR.equals(persona.getTipoPersona())) {
				return finalizarSolicitudPaseador(persona, redirectAttributes, model);
			}
			return finalizarRegistroCliente(usuarioEnt, personaentity, redirectAttributes, model);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return "landing/usuario-crear-cliente";
		}
	}

	public String finalizarSolicitudPaseador(PersonaDetalleRequest persona,
			RedirectAttributes redirectAttributes, Model model) {
		String htmlContent = new String(parametroService.getParametro(KEY_MAIL_INFO_PASEADOR).getArchivos(),
				StandardCharsets.UTF_8);
		String correoAdministrador=parametroService.getParametro(KEY_MAIL_ADMINISTRADOR).getValorText();
		try {
			htmlContent = htmlContent.replace("{{nombres}}", persona.getNombres() + " " + persona.getApellidos());
			htmlContent = htmlContent.replace("{{identificacion}}", persona.getIdentificacion());
			htmlContent = htmlContent.replace("{{email}}", persona.getEmail());
			htmlContent = htmlContent.replace("{{celular}}", persona.getCelular());
			htmlContent = htmlContent.replace("{{mesesExperiencia}}", persona.getAniosExperiencia().toString());
			htmlContent = htmlContent.replace("{{experienciaPrevia}}", persona.getExperienciaPrevia());
			StringBuilder recordsTable = new StringBuilder();
			recordsTable.append("<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>");
			recordsTable.append("<tr style='background-color: #ddd; color: #000; text-align: left;'>");
			recordsTable.append("<th style='border: none; padding: 8px;'>Nivel Academico</th>");
			recordsTable.append("<th style='border: none; padding: 8px;'>Institucion</th>");
			recordsTable.append("<th style='border: none; padding: 8px;'>Graduacion</th>");
			recordsTable.append("<th style='border: none; padding: 8px;'>Estado</th>");
			recordsTable.append("</tr>");

			for (RecordAcademicoRequest record : persona.getRecordsAcademicosList()) {
				recordsTable.append("<tr>");
				recordsTable.append("<td style='border: none; padding: 8px;'>" + record.getNivelAcademico() + "</td>");
				recordsTable.append("<td style='border: none; padding: 8px;'>" + record.getInstitution() + "</td>");
				recordsTable.append("<td style='border: none; padding: 8px;'>" + record.getAnioGraduacion() + "</td>");
				recordsTable.append("<td style='border: none; padding: 8px;'>" + record.getEstadoAcademico() + "</td>");
				recordsTable.append("</tr>");
			}

			recordsTable.append("</table>");

			htmlContent = htmlContent.replace("{{recordsAcademicos}}", recordsTable.toString());
			htmlContent = htmlContent.replace("{{tamanosAceptados}}",
					persona.getTamanosAceptados().stream().map(Enum::name).collect(Collectors.joining(", ")));

			emailSender.sendEmail(correoAdministrador, "SOLICITUD DE PASEADOR", htmlContent);
			redirectAttributes.addFlashAttribute("info",
					"Tu solicitud está siendo procesada espera la confirmación por el administrador.");
			return "redirect:/auth/login";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return "landing/usuario-crear-cliente";
		}
	}

	public String finalizarRegistroCliente(Usuariodetalle usuarioEnt, Personadetalle personaentity,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			String token = generarTokenRecuperacion();
			guardarToken(usuarioEnt, token);
			String urlActivacion = parametroService.getParametro(URL_DOMAIN_MAIL).getValorText()
					+ "/auth/activarUsuario?token=" + token;
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_CLIENTE).getArchivos(),
					StandardCharsets.UTF_8);
			htmlContent = htmlContent.replace("{{enlace}}", urlActivacion);

			emailSender.sendEmail(personaentity.getEmail(), "ACTIVACIÓN DE CUENTA", htmlContent);
			redirectAttributes.addFlashAttribute("info", "Se ha enviado un correo electronico para activar la cuenta.");
			return "redirect:/auth/login";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return "landing/usuario-crear-cliente";
		}
	}

	/*-----------------------CREAR NUEVO CLIENTE ------------------------*/
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.addCookie(new Cookie("JSESSIONID", null));
		return "redirect:/auth/login";
	}

	@GetMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		this.setUserInSession(request);
		if (request.isUserInRole("ADMIN"))
			log.info("Ud es administrador");

		if (request.isUserInRole("CLIENTE"))
			log.info("Ud es cliente");

		if (request.isUserInRole("PASEADOR"))
			log.info("Ud es paseador");

		return "redirect:/home";
	}

	/**
	 * Set user in session
	 * 
	 * @param request
	 */
	private void setUserInSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		PersonaDetalleRequest persona = (PersonaDetalleRequest) session.getAttribute("loggedInUser");
		if (persona == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Personadetalle user = userService.buscarPorEmailOrUserName(auth.getName()).getIdpersona();
			PersonaDetalleRequest authUser = new PersonaDetalleRequest();
			authUser.setId(user.getId());
			authUser.setNombres(user.getNombres().toUpperCase());
			authUser.setApellidos(user.getApellidos().toUpperCase());
			authUser.setIdentificacion(user.getIdentificacion());
			authUser.setCelular(user.getCelular());
			authUser.setEmail(user.getEmail());
			this.setMenuItemsInSession(session, auth);
			session.setAttribute("loggedInUser", authUser);
		}
	}

	/**
	 * Set menu items in session
	 * 
	 * @param session
	 * @param auth
	 */
	private void setMenuItemsInSession(HttpSession session, Authentication auth) {
		Set<String> roles = auth.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority())
				.collect(Collectors.toSet());
		Set<MenuItem> menuItems = menuService.getMenuByRoles(roles);
		session.setAttribute("menuItems", menuItems);
	}

	// RECUPERAR CONTRASEÑA
	@GetMapping("/recupera")
	public String mostrarFormularioRecuperacion() {
		return "landing/recuperar-pass";
	}

	// Paso 1: Validar y enviar enlace de recuperación
	@PostMapping("/validamail")
	public String buscarMail(@RequestParam("email") String email, Model model) {
		try {
			String token = generarTokenRecuperacion();
			Usuariodetalle usuario = userService.buscarPorEmailOrUserName(email);
			if (usuario == null) {
				model.addAttribute("error", "El correo electronico no se encuentra registrado.");
				return "landing/recuperar-pass";
			}
			if(!usuario.getEstado()) {
				model.addAttribute("error", "Lo lamentamos, pero no puedes solicitar la recuperación de la contraseña tu usuario fué deshabilitado.");
				return "landing/recuperar-pass";
			}
			guardarToken(usuario, token);
			enviarCorreoRecuperacion(email, token);
			model.addAttribute("mensaje", "Se ha enviado un enlace de recuperación a su correo.");
			return "landing/index";
		} catch (Exception e) {
			model.addAttribute("error", "Ocurrio un problema inesperado, comuniquese con el administrador.");
			return "landing/recuperar-pass";
		}
	}

	@GetMapping("/activarUsuario")
	public String activarUsuario(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
		Usuariodetalle usuario = userService.buscarToken(token);
		if (usuario != null) {
			usuario.setEstado(Boolean.TRUE);
			userService.insertarUsuarioDetalle(usuario);
			redirectAttributes.addFlashAttribute("info", "Su usuario ha sido activado con exito.");
			return "redirect:/auth/login";
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "No se pudo comprobar la cuenta registrada.");
			return "redirect:/auth/login";
		}
	}

	// Paso 2: Mostrar formulario para restablecer la contraseña
	@GetMapping("/restablecer")
	public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
		Usuariodetalle usuario = userService.buscarToken(token);
		if (usuario != null) {
			if (!isTokenValid(usuario.getToken())) {
				model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
				return "landing/recuperar-pass";
			}
		} else {
			model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
			return "landing/recuperar-pass";
		}
		ChangePasswordRequest changePasswordReq = new ChangePasswordRequest();
		changePasswordReq.setUser(usuario);
		model.addAttribute("changePass", changePasswordReq);
		return "landing/restablecer";
	}

	// Paso 3: Procesar el restablecimiento de contraseña
	@PostMapping("/cambiarPass")
	public String restablecerContrasena(@ModelAttribute("changePass") ChangePasswordRequest changePasswordReq,
			Model model) throws UnsupportedEncodingException, MessagingException {
		Usuariodetalle usuario = changePasswordReq.getUser();
		// Encriptar y guardar la nueva contraseña
		usuario.setPassword(encriptarContrasena(changePasswordReq.getPassword()));
		userService.insertarUsuarioDetalle(usuario);
		String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(),
				StandardCharsets.UTF_8);
		String mensajeDinamico = "BIENVENIDO A LA FUNDACION ARNUV! <br>TU CAMBIO DE CONTRASEÑA FUE EXITOSA <br>"
				+ usuario.getIdpersona().getNombres() + " " + usuario.getIdpersona().getApellidos();
		htmlContent = htmlContent.replace("{{mensajeBienvenida}}",
				"<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">"
						+ mensajeDinamico.toUpperCase() + "</span></p>");
		emailSender.sendEmail(usuario.getIdpersona().getEmail(), "CAMBIO DE CONTRASEÑA", htmlContent);
		model.addAttribute("mensaje", "Su contraseña ha sido restablecida con éxito.");
		return "landing/login";
	}

	private String generarTokenRecuperacion() {
		return UUID.randomUUID().toString();
	}

	private void guardarToken(Usuariodetalle usuario, String token) {
		Instant now = Instant.now();
		Instant expirationTime = now.plusSeconds(convertMinutesToSeconds(DEFAULT_MINUTES_EXPIRATION));
		Parametros parametro = parametroService.getParametro(KEY_MINUTES_EXPIRATION);
		if (parametro != null) {
			expirationTime = now.plusSeconds(convertMinutesToSeconds(parametro.getValorNumber().intValue()));
		}
		Token nuevoToken = new Token();
		nuevoToken.setToken(token);
		nuevoToken.setStartDate(now);
		nuevoToken.setEndDate(expirationTime);
		nuevoToken.setEstado(Boolean.TRUE);
		usuario.setToken(nuevoToken);
		userService.insertarUsuarioDetalle(usuario);
	}

	public boolean isTokenValid(Token token) {
		if (token == null) {
			return false;
		}
		Instant now = Instant.now();
		return now.isBefore(token.getEndDate());
	}

	private void enviarCorreoRecuperacion(String email, String token) throws Exception {
		String urlRecuperacion = parametroService.getParametro(URL_DOMAIN_MAIL).getValorText()
				+ "/auth/restablecer?token=" + token;
		String htmlContent = new String(parametroService.getParametro(KEY_MAIL_RECUPERACION_PASSWORD).getArchivos(),
				StandardCharsets.UTF_8);
		
		htmlContent = htmlContent.replace("{{enlace}}",urlRecuperacion);
		emailSender.sendEmail(email, "RECUPERACIÓN DE PASSWORD", htmlContent);
	}

	private String encriptarContrasena(String contrasena) {
		return passwordEncoder.encode(contrasena);
	}
}