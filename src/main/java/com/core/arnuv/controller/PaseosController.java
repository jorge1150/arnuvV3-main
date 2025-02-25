package com.core.arnuv.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.core.arnuv.model.Calificacion;
import com.core.arnuv.model.Parametros;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.BusquedaFechasResponse.BusquedaFechaeDto;
import com.core.arnuv.service.ICalificacionService;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.services.imp.EmailSender;
import com.core.arnuv.utils.ArnuvUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import static com.core.arnuv.constants.Constants.*;

@Controller
@RequestMapping("/paseo")
@RequiredArgsConstructor
@Slf4j
public class PaseosController {
	private final IPaseoService paseoService;
	private final IPersonaDetalleService personaDetalleService;
	private final ITarifarioService ITarifarioService;
	private final IMascotaDetalleService mascotaDetalleService;
	private final IUbicacionService ubicacionService;
	private final IParametroService parametroService;
	private final ArnuvUtils arnuvUtils;
	private final EmailSender emailSender;
	private final ICalificacionService calificacionService;
	public final IMascotaDetalleService mscotaDetalleService;

	@GetMapping("/listar")
	public String listar(Model model, HttpServletRequest request) {
		var idusuariologueado = arnuvUtils.getLoggedInUsername();

		if (request.isUserInRole("ADMIN")) {
			List<Paseo> listapaseos = paseoService.listarPaseos();
			model.addAttribute("lista", listapaseos);
			return "content-page/paseo-listar";
		}
		if (request.isUserInRole("CLIENTE")) {
			List<Paseo> listapaseos = paseoService.buscarpersonacliente(idusuariologueado.getId());
			model.addAttribute("lista", listapaseos);
			return "content-page/paseo-listar";
		}
		if (request.isUserInRole("PASEADOR")) {
			List<Paseo> listapaseos = paseoService.buscaridpersonapasedor(idusuariologueado.getId());
			model.addAttribute("lista", listapaseos);
			return "content-page/paseo-paseador-listar";
		}
		return "redirect:/home";
	}

	@GetMapping("/buscarPorFecha")
	public String buscarPorFecha(Model model) {
		BusquedaFechaeDto busquedaFechasResponse = new BusquedaFechaeDto();
		model.addAttribute("nuevo", busquedaFechasResponse);
		return "content-page/paseo-listar-por-fecha"; // Cambia esto por el nombre de la vista Thymeleaf que estás
														// usando
	}

	@GetMapping("/ListarbuscarPorFecha")
	public String ListarbuscarPorFecha(
			@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin, Model model,
			HttpServletRequest request) {
		Date fechaIni = Date.from(fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date fechaFinal = Date.from(fechaFin.atStartOfDay(ZoneId.systemDefault()).toInstant());
		var idusuariologueado = arnuvUtils.getLoggedInUsername();
		if (request.isUserInRole("CLIENTE")) {
			List<Paseo> listapaseos = paseoService.buscarRangoFechasCliente(fechaIni, fechaFinal,
					idusuariologueado.getId());
			model.addAttribute("lista", listapaseos);
			return "content-page/paseo-listar-por-fecha";
		}
		if (request.isUserInRole("PASEADOR")) {
			List<Paseo> listapaseos = paseoService.buscarRangoFechasPaseador(fechaIni, fechaFinal,
					idusuariologueado.getId());
			model.addAttribute("lista", listapaseos);
			return "content-page/paseo-listar-por-fecha";
		}
		return "content-page/paseo-listar-por-fecha";
	}

	@GetMapping("/nuevo")
	public String crear(Model model, HttpServletRequest request) {
		var idusuariologueado = arnuvUtils.getLoggedInUsername();
		model.addAttribute("nuevo", new Paseo());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.buscarPorEstado(true));
		model.addAttribute("mascota", mascotaDetalleService.findByIdpersonaId(idusuariologueado.getId()));

		Ubicacion ubicacionDefault = ubicacionService.ubicacionPersonaPorDefecto(idusuariologueado.getId());
		model.addAttribute("ubicacionDefault", ubicacionDefault);
		model.addAttribute("listaPaseadores",
				ubicacionService.findUbicacionesEnRangoKmPaseadores(ubicacionDefault.getLatitud(),
						ubicacionDefault.getLongitud(), parametroService.getParametro(KM_BUSQUEDA).getValorNumber()));
		model.addAttribute("vistaMapa", parametroService.getParametro(VISTA_MAPA).getValorNumber());
		if (request.isUserInRole("ADMIN")) {
			model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
			model.addAttribute("cliente", personaDetalleService.listarTodosPersonaDetalle());
		}
		return "content-page/paseo-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") Paseo nuevo, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
		String formattedDate = formatter.format(date);
		if (request.isUserInRole("CLIENTE")) {
			Personadetalle personaCLiente = new Personadetalle();
			var idusuariologueado = arnuvUtils.getLoggedInUsername().getId();
			personaCLiente.setId(idusuariologueado);
			nuevo.setIdpersonacliente(personaCLiente);
		}
		Personadetalle personadetalle = personaDetalleService.buscarPorId(nuevo.getIdpersonapasedor().getId());

		String fechaRealInicio = nuevo.getFecharealinicio().toString();
		String fechaEspanol = Strings.EMPTY;
		try {
			Date fecha = FECHA_FORMATO_ENTRADA.parse(fechaRealInicio);
			fechaEspanol = FECHA_FORMATO_SALIDA.format(fecha);
		} catch (ParseException e) {
			log.error("Ocurrio un error: {}", e.getMessage());
		}
		if (nuevo.getEstado().equals(ESTADO_PENDIENTE)) {
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_SOLICITUD_PASEO).getArchivos(),
					StandardCharsets.UTF_8);
			var mascota = mscotaDetalleService
					.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
			var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
			String nombreMascota = mascota != null ? mascota.getNombre() : "";
			String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
			String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
			String edad = mascota != null ? mascota.getEdad().toString() : "";
			htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
			htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
			htmlContent = htmlContent.replace("{{edad}}", edad);
			htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
			htmlContent = htmlContent.replace("{{observaciones}}",
					nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
			emailSender.sendEmail(personadetalle.getEmail(), "SOLICITUD DE SERVICIO", htmlContent);
		}
		if (nuevo.getEstado().equals(ESTADO_APROBADO)) {
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_APROBACION_PASEO).getArchivos(),
					StandardCharsets.UTF_8);
			var mascota = mscotaDetalleService
					.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
			var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
			var personaCliente = personaDetalleService.buscarPorId(nuevo.getIdpersonacliente().getId());
			String nombreMascota = mascota != null ? mascota.getNombre() : "";
			String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
			String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
			String edad = mascota != null ? mascota.getEdad().toString() : "";
			htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
			htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
			htmlContent = htmlContent.replace("{{edad}}", edad);
			htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
			htmlContent = htmlContent.replace("{{observaciones}}",
					nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
			emailSender.sendEmail(personaCliente.getEmail(), "CONFIRMACION DE SERVICIO", htmlContent);
			String correoAdministrador = parametroService.getParametro(KEY_MAIL_ADMINISTRADOR).getValorText();
			emailSender.sendEmail(correoAdministrador, "CONFIRMACION DE SERVICIO", htmlContent);
		}
		if (nuevo.getEstado().equals(ESTADO_RECHAZADO)) {
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_RECHAZO_PASEO).getArchivos(),
					StandardCharsets.UTF_8);
			var mascota = mscotaDetalleService
					.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
			var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
			var personaCliente = personaDetalleService.buscarPorId(nuevo.getIdpersonacliente().getId());
			String nombreMascota = mascota != null ? mascota.getNombre() : "";
			String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
			String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
			String edad = mascota != null ? mascota.getEdad().toString() : "";
			htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
			htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
			htmlContent = htmlContent.replace("{{edad}}", edad);
			htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
			htmlContent = htmlContent.replace("{{observaciones}}",
					nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
			emailSender.sendEmail(personaCliente.getEmail(), "RECHAZO DE SERVICIO", htmlContent);
			String correoAdministrador = parametroService.getParametro(KEY_MAIL_ADMINISTRADOR).getValorText();
			emailSender.sendEmail(correoAdministrador, "RECHAZO DE SERVICIO", htmlContent);
		}

		if (nuevo.getEstado().equals(ESTADO_PASEO_FINALIZADO)) {
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_PASEO_FINALIZADO).getArchivos(),
					StandardCharsets.UTF_8);
			var mascota = mscotaDetalleService
					.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
			var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
			var personaCliente = personaDetalleService.buscarPorId(nuevo.getIdpersonacliente().getId());
			String nombreMascota = mascota != null ? mascota.getNombre() : "";
			String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
			String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
			String edad = mascota != null ? mascota.getEdad().toString() : "";
			htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
			htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
			htmlContent = htmlContent.replace("{{edad}}", edad);
			htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
			htmlContent = htmlContent.replace("{{observaciones}}",
					nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
			emailSender.sendEmail(personaCliente.getEmail(), "EL PASEO FINALIZO", htmlContent);
		}

		if (nuevo.getEstado().equals(ESTADO_FINALIZADO)) {
			String htmlContent = new String(parametroService.getParametro(KEY_MAIL_CALIFICACION_PASEO).getArchivos(),
					StandardCharsets.UTF_8);
			var mascota = mscotaDetalleService
					.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
			var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
			var paseador = personaDetalleService.buscarPorId(nuevo.getIdpersonapasedor().getId());
			String nombreMascota = mascota != null ? mascota.getNombre() : "";
			String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
			String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
			String edad = mascota != null ? mascota.getEdad().toString() : "";
			htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
			htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
			htmlContent = htmlContent.replace("{{edad}}", edad);
			htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
			htmlContent = htmlContent.replace("{{observaciones}}",
					nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
			emailSender.sendEmail(paseador.getEmail(), "GRACIAS POR TU TRABAJO", htmlContent);
		}

		paseoService.insertarPaseo(nuevo);
		return "redirect:/paseo/listar";

	}

	@GetMapping("/editar/{idpaseo}")
	public String editar(@PathVariable(value = "idpaseo") int codigo, Model model, HttpServletRequest request) {
		var idusuariologueado = arnuvUtils.getLoggedInUsername();
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.findByIdpersonaId(idusuariologueado.getId()));
		Ubicacion ubicacionDefault = ubicacionService.ubicacionPersonaPorDefecto(idusuariologueado.getId());
		model.addAttribute("ubicacionDefault", ubicacionDefault);
		model.addAttribute("listaPaseadores",
				ubicacionService.findUbicacionesEnRangoKmPaseadores(ubicacionDefault.getLatitud(),
						ubicacionDefault.getLongitud(), parametroService.getParametro(KM_BUSQUEDA).getValorNumber()));
		model.addAttribute("vistaMapa", parametroService.getParametro(VISTA_MAPA).getValorNumber());
		if (request.isUserInRole("ADMIN")) {
			model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
			model.addAttribute("cliente", personaDetalleService.listarTodosPersonaDetalle());
		}
		return "content-page/paseo-crear";
	}

	@GetMapping("/editarPaseador/{idpaseo}")
	public String editarPaseador(@PathVariable(value = "idpaseo") int codigo, Model model) {
		var idusuariologueado = arnuvUtils.getLoggedInUsername();
		Ubicacion ubicacionDefault = ubicacionService.ubicacionPersonaPorDefecto(idusuariologueado.getId());
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		Ubicacion ubicacionCliente = ubicacionService
				.ubicacionPersonaPorDefecto(itemrecuperado.getIdpersonacliente().getId());
		model.addAttribute("ubicacionCliente", ubicacionCliente);
		model.addAttribute("ubicacionDefault", ubicacionDefault);
		model.addAttribute("vistaMapa", parametroService.getParametro(VISTA_MAPA).getValorNumber());
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		return "content-page/paseo-paseador-crear";
	}

	@GetMapping("/editarCliente/{idpaseo}")
	public String editarCliente(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		System.out.println(itemrecuperado.getEstado());
		Calificacion calificacion = calificacionService.findByIdpaseoId(codigo);
		String errorMessag = Strings.EMPTY;
		if (calificacion == null && itemrecuperado.getEstado().equals(ESTADO_PASEO_FINALIZADO)) {
			errorMessag = "Tienes que calificar el paseo antes de finalizar";
		}
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("paseoID", itemrecuperado.getId());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		model.addAttribute("error", errorMessag);
		return "content-page/paseo-cliente-ver";
	}

	@PostMapping("/finalizarPaseo")
	public String finalizarPaseo(@ModelAttribute("nuevo") Paseo nuevo, HttpServletRequest request, Model model)
			throws UnsupportedEncodingException, MessagingException {
		Calificacion calificacion = calificacionService.findByIdpaseoId(nuevo.getId());
		if (calificacion == null) {
			model.addAttribute("error", "Tienes que calificar el paseo antes de finalizar");
			return "redirect:/paseo/editarCliente/".concat(String.valueOf(nuevo.getId()));
		} else {

			Personadetalle personadetalle = personaDetalleService.buscarPorId(nuevo.getIdpersonapasedor().getId());

			String fechaRealInicio = nuevo.getFecharealinicio().toString();
			String fechaEspanol = Strings.EMPTY;
			try {
				Date fecha = FECHA_FORMATO_ENTRADA.parse(fechaRealInicio);
				fechaEspanol = FECHA_FORMATO_SALIDA.format(fecha);
			} catch (ParseException e) {
				log.error("Ocurrio un error: {}", e.getMessage());
			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
			String formattedDate = formatter.format(date);

			if (nuevo.getEstado().equals(ESTADO_FINALIZADO)) {
				String htmlContent = new String(
						parametroService.getParametro(KEY_MAIL_CALIFICACION_PASEO).getArchivos(),
						StandardCharsets.UTF_8);
				var mascota = mscotaDetalleService
						.buscarMascotaID(Integer.parseInt(nuevo.getIdMascota().getIdmascota().toString()));
				var tarifa = ITarifarioService.buscarPorId(Integer.parseInt(nuevo.getIdtarifario().getId().toString()));
				var paseador = personaDetalleService.buscarPorId(nuevo.getIdpersonapasedor().getId());

				String nombreMascota = mascota != null ? mascota.getNombre() : "";
				String razaMascota = mascota != null ? mascota.getFkcatalogodetalle().getNombre() : "";
				String tamano = mascota != null ? mascota.getTamanoPerro().toString() : "";
				String edad = mascota != null ? mascota.getEdad().toString() : "";
				htmlContent = htmlContent.replace("{{fecha}}", fechaEspanol);
				htmlContent = htmlContent.replace("{{mascota}}", nombreMascota + " " + razaMascota + " " + tamano);
				htmlContent = htmlContent.replace("{{edad}}", edad);
				htmlContent = htmlContent.replace("{{tarifa}}", "$ " + tarifa.getPrecio());
				htmlContent = htmlContent.replace("{{calificacion}}", String.valueOf(calificacion.getCalificacion()));
				htmlContent = htmlContent.replace("{{observaciones}}",
						nuevo.getObservacionpaseo() != null ? nuevo.getObservacionpaseo() : "N/A");
				emailSender.sendEmail(paseador.getEmail(), "GRACIAS POR TU TRABAJO", htmlContent);
			}
			paseoService.insertarPaseo(nuevo);
			return "redirect:/paseo/listar";
		}
	}
	@GetMapping("/cancelar/{codigo}")
	public String cancelar(@PathVariable(value = "codigo") int codigo, Model model) throws UnsupportedEncodingException, MessagingException {
		var paseo=paseoService.buscarPorId(codigo);
		paseo.setEstado(ESTADO_CANCELADO);
		String htmlContent = new String(parametroService.getParametro(KEY_MAIL_CANCELACION_PASEO).getArchivos(),
				StandardCharsets.UTF_8);
		String fechaEspanol = Strings.EMPTY;
		DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter formatterSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		try {
		    LocalDateTime fecha = LocalDateTime.parse(paseo.getFecharealinicio().toString(), formatterEntrada);
		    fechaEspanol = fecha.format(formatterSalida);
		} catch (DateTimeParseException e) {
		    log.error("Ocurrió un error al parsear la fecha: {}", e.getMessage());
		}
		var mascota=mscotaDetalleService.buscarMascotaID(Integer.parseInt(paseo.getIdMascota().getIdmascota().toString()));
		var tarifa=ITarifarioService.buscarPorId(Integer.parseInt(paseo.getIdtarifario().getId().toString()));
		var paseador = personaDetalleService.buscarPorId(paseo.getIdpersonapasedor().getId());
		
		String nombreMascota=mascota!=null?mascota.getNombre():"";
		String razaMascota=mascota!=null?mascota.getFkcatalogodetalle().getNombre():"";
		String tamano=mascota!=null?mascota.getTamanoPerro().toString():"";
		String edad=mascota!=null?mascota.getEdad().toString():"";
		htmlContent = htmlContent.replace("{{fecha}}",fechaEspanol);
		htmlContent = htmlContent.replace("{{mascota}}", nombreMascota+" "+razaMascota +" "+tamano);
		htmlContent = htmlContent.replace("{{edad}}",edad);
		htmlContent = htmlContent.replace("{{tarifa}}","$ "+tarifa.getPrecio());
		htmlContent = htmlContent.replace("{{observaciones}}",paseo.getObservacionpaseo()!=null?paseo.getObservacionpaseo():"N/A");
		emailSender.sendEmail(paseador.getEmail(), "CANCELACION DE SERVICIO", htmlContent);
		paseoService.insertarPaseo(paseo);
		return "redirect:/paseo/listar";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
