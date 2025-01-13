package com.core.arnuv.controller;

import com.core.arnuv.model.Parametros;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import com.core.arnuv.services.imp.EmailSender;
import com.core.arnuv.utils.ArnuvNotFoundException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.core.arnuv.constants.Constants.*;

@Controller
@RequestMapping("/persona")
@RequiredArgsConstructor
@Slf4j
public class PersonaController {
	private final IPersonaDetalleService servicioPersonaDetalle;
	private final IUbicacionService  ubicacionService;
	private final IParametroService parametroService;
	private final IUsuarioDetalleService usuarioDetalleService;
	private final IUsuarioRolService usuarioRolService;
	private final EmailSender emailSender;

	
	
	@GetMapping("listar")
	public String personListar(Model model) {		
		List<Personadetalle> listaPersonas = servicioPersonaDetalle.listarTodosPersonaDetalle();
		model.addAttribute("lista", listaPersonas);
		return "content-page/persona-listar";
	}
	
	
	@GetMapping("crear")
	public String personCreate(Model model) {
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);		
		return "content-page/persona-crear";
	}

	@PostMapping("create-access")
	private String personCreateAccess(@ModelAttribute("nuevo") PersonaDetalleRequest persona, Model model) {
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);		
		Personadetalle personadetalle = persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion", "iddetalleidentificacion");
		Personadetalle personaEntity;
		try {
			personadetalle.setFechaingreso(new Date());
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
			if (e.getMessage().contains("uk_eqrqigy92n8fi43p0e9pmf9aw")) { // Email
				errorMessage = "Error al guardar datos: Ya existe el email registrado=" + persona.getEmail();
			} else if (e.getMessage().contains("uk_q5r1m95xoe8hnuv378tdsymul")) { // Celular
				errorMessage = "Error al guardar datos: Ya existe el celular registrado=" + persona.getCelular();
			} else if (e.getMessage().contains("uk_jmjk4q6y2fnm48qlml12e5cl9")) { // Identificación
				errorMessage = "Error al guardar datos: Ya existe la identificacion registrada=" + persona.getIdentificacion();
			} else {
				// Mensaje genérico si no se detecta un campo específico
				errorMessage = "Error al guardar datos: Se ha detectado un problema con los datos ingresados.";
			}
			model.addAttribute("error", errorMessage);
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
    private String inavilitaUsuario(@ModelAttribute("nuevo") Personadetalle nuevo) throws UnsupportedEncodingException, MessagingException {
		Personadetalle personadetalle = servicioPersonaDetalle.buscarPorIdentificacion(nuevo.getIdentificacion());
		Usuariodetalle usuariodetalle = usuarioDetalleService.buscarpersona(personadetalle.getId());
		
		
		String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(), StandardCharsets.UTF_8);


		String fechaEspanol= Strings.EMPTY;
		String mensajeDinamico="";

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
		String formattedDate = formatter.format(date);
		fechaEspanol =formattedDate;

		Boolean estado = null;
		if (usuariodetalle.getEstado()==null){
			estado=true;
			mensajeDinamico = "SU USUARIO A SIDO HABILITADO"+" FECHA:" + fechaEspanol+", PUEDES VOLVER INGRESAR AL SISTEMA ARNUV !!";
		}

		if (Boolean.TRUE.equals(usuariodetalle.getEstado())){
			estado =null;
			mensajeDinamico = "SU  USUARIO A SIDO DESHABILITADO"+" FECHA:" + fechaEspanol+", CONTACTATE CON EL ADMINSTRADOR DEL SISTEMA ARNUV !!";

		}
		usuariodetalle.setEstado(estado);
		usuarioDetalleService.actualizarUsuarioDetalle(usuariodetalle);

		htmlContent = htmlContent.replace("{{mensajeBienvenida}}", "<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">" + mensajeDinamico.toUpperCase() + "</span></p>");
		emailSender.sendEmail(personadetalle.getEmail(), "FUNDACIÓN ARNUV", htmlContent);

        return "redirect:/persona/listar";
    }
}