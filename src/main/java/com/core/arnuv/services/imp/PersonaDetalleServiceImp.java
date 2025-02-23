package com.core.arnuv.services.imp;

import com.core.arnuv.constants.Constants;
import com.core.arnuv.enums.RolEnum;
import com.core.arnuv.model.*;
import com.core.arnuv.repository.IPersonaDetalleRepository;
import com.core.arnuv.repository.IUsuarioRolRepository;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.RecordAcademicoRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Component
@RequiredArgsConstructor
public class PersonaDetalleServiceImp implements IPersonaDetalleService {
	private final IPersonaDetalleRepository repo;
	private final IUbicacionService ubicacionService;
	private final PasswordEncoder passwordEncoder;
	private final IUsuarioDetalleService userService;
	private final IRolService servicioRol;
	private final IUsuarioRolService servicioUsuarioRol;
	private final IUsuarioRolRepository repoRol;
	private final IRecordAcademicoService academicoService;

	@Override
	public List<Personadetalle> listarTodosPersonaDetalle() {
		return repo.findAll();
	}

	@Override
	public Personadetalle insertarPersonaDetalle(Personadetalle data) {
		return repo.save(data);
	}

	@Override
	public Personadetalle actualizarPersonaDetalle(Personadetalle data) {
		Personadetalle existePersonaDetalle = repo.findById(data.getId()).orElse(null);
		existePersonaDetalle.setIdusuarioing(data.getIdusuarioing());
		existePersonaDetalle.setIdusuariomod(data.getIdusuariomod());
		existePersonaDetalle.setFechaingreso(data.getFechaingreso());
		existePersonaDetalle.setFechamodificacion(data.getFechamodificacion());
		existePersonaDetalle.setNombres(data.getNombres());
		existePersonaDetalle.setApellidos(data.getApellidos());
		existePersonaDetalle.setIdentificacion(data.getIdentificacion());
		existePersonaDetalle.setCelular(data.getCelular());
		existePersonaDetalle.setEmail(data.getEmail());
		return repo.save(existePersonaDetalle);
	}

	@Override
	public Personadetalle buscarPorId(int id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public Personadetalle buscarPorIdentificacion(String identificacion) {
		return repo.buscarPorIdentificacion(identificacion);
	}

	@Override
	public boolean eliminar(Personadetalle data) {
		repo.delete(data);
		return true;
	}

	@Override
	public Personadetalle buscarEmail(String email) {
		return repo.buscarEmail(email);
	}

	@Override
	public String verificarDuplicados(String email, String celular, String identificacion) {
		String error = Strings.EMPTY;
		if (buscarEmail(email) != null) {
			error = "El correo electronico ya se encuentra registrado";
		}
		if (buscarPorIdentificacion(identificacion) != null) {
			error = "La identificación ya se encuentra registrada";
		}
		if (buscarPorCelular(celular) != null) {
			error = "El celular ya se encuentra registrado";
		}
		return error;
	}

	@Override
	public Personadetalle buscarPorCelular(String celular) {
		return repo.findByCelular(celular);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Personadetalle guardarInformacionCompleta(PersonaDetalleRequest persona, UsuarioDetalleRequest usuario,
			List<RecordAcademicoRequest> recordAcademico) throws Exception {
		var personaEnt = persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion",
				"iddetalleidentificacion");
		List<RecordAcademico> listRecord = new ArrayList<>();
		try {
			personaEnt.setFechaingreso(new Date());
			personaEnt = insertarPersonaDetalle(personaEnt);
			crearUbicacion(persona, personaEnt);
			if (Constants.PASEADOR.equals(persona.getTipoPersona())) {
				for (RecordAcademicoRequest item : persona.getRecordsAcademicosList()) {
					var recordEnt = item.mapearDato(item, RecordAcademico.class, "");
					listRecord.add(recordEnt);
				}
				crearRecordAcademico(personaEnt, listRecord);
			}
			crearUsuario(personaEnt, usuario, persona.getTipoPersona());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return personaEnt;
	}

	/**
	 * CREAR RECORDACADEMICO
	 * 
	 * @param personaEnt
	 * @param recordAcademicoEnt
	 */
	private void crearRecordAcademico(Personadetalle personaEnt, List<RecordAcademico> recordAcademicoEnt) {
		for (RecordAcademico recordAcademico : recordAcademicoEnt) {
			var recordA = new RecordAcademico();
			recordA.setAnioGraduacion(recordAcademico.getAnioGraduacion());
			recordA.setEstadoAcademico(recordAcademico.getEstadoAcademico());
			recordA.setEstaEliminado(recordAcademico.getEstaEliminado());
			recordA.setInstitution(recordAcademico.getInstitution());
			recordA.setNivelAcademico(recordAcademico.getNivelAcademico());
			recordA.setPersona(personaEnt);
			academicoService.guardarRecordAcademico(recordA);
		}
	}

	/**
	 * CREAR UBICACION
	 * 
	 * @param persona
	 * @param personaEnt
	 */
	private void crearUbicacion(PersonaDetalleRequest persona, Personadetalle personaEnt) {
		var ubicacion = new Ubicacion();
		ubicacion.setLatitud(persona.getLatitud());
		ubicacion.setLongitud(persona.getLongitud());
		ubicacion.setIsDefault(1);
		ubicacion.setIdpersona(personaEnt);
		ubicacionService.insertarUbicacion(ubicacion);
	}

	/**
	 * CREAR USUARIO
	 * 
	 * @param persona
	 * @param usuario
	 * @throws Exception
	 */
	private Usuariodetalle crearUsuario(Personadetalle persona, UsuarioDetalleRequest usuario, String tipo)
			throws Exception {
		Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
		usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuariodetalle.setEstado(Boolean.FALSE);
		usuariodetalle.setIdpersona(persona);
		usuariodetalle.setFechaingreso(new Date());
		if (userService.buscarPorEmailOrUserName(usuario.getUsername()) != null) {
			throw new Exception("El nombre de usuario ya se encuentra registrado.");
		}
		Usuariodetalle usuarioEnt = userService.insertarUsuarioDetalle(usuariodetalle);
		var role = Constants.PASEADOR.equals(tipo) ? RolEnum.ROLE_WALKER : RolEnum.ROLE_USER;
		crearRol(usuarioEnt, role);
		return usuarioEnt;
	}

	/**
	 * CREAR ROL DEL USUARIO
	 * 
	 * @param usuario
	 * @param role
	 */
	private void crearRol(Usuariodetalle usuario, RolEnum role) {
		var rolentity = servicioRol.findByNombre(role.getValue());
		UsuariorolId usuariorolId = new UsuariorolId();
		usuariorolId.setIdusuario(usuario.getIdusuario());
		usuariorolId.setIdrol(rolentity.getId());
		Usuariorol usuariorol = new Usuariorol();
		usuariorol.setIdrol(rolentity);
		usuariorol.setIdusuario(usuario);
		usuariorol.setId(usuariorolId);
		servicioUsuarioRol.insertarUsuarioRol(usuariorol);
	}

	@Override
	public Set<Personadetalle> listarPaseadores() {
		return repoRol.getAllByRolePerson(RolEnum.ROLE_WALKER.getValue());
	}

	@Override
	public Set<Personadetalle> listarClientes() {
		return repoRol.getAllByRolePerson(RolEnum.ROLE_USER.getValue());
	}

	@Override
	public Set<Personadetalle> listarAdministradores() {
		return repoRol.getAllByRolePerson(RolEnum.ROLE_ADMIN.getValue());
	}

	@Override
	public Set<Personadetalle> listarPersonas() {
		return new HashSet<>(repo.findAll());
	}

	@Override
	public String verificarCorreo(String email) {
		String error = Strings.EMPTY;
		if (buscarEmail(email) != null) {
			error = "El correo electronico ya se encuentra registrado";
		}
		return error;
	}

	@Override
	public String verificarTelefono(String celular) {
		String error = Strings.EMPTY;
		if (buscarPorCelular(celular) != null) {
			error = "El celular ya se encuentra registrado";
		}
		return error;
	}
}