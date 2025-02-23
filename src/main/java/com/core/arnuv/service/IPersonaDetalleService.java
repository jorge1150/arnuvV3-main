package com.core.arnuv.service;

import java.util.List;
import java.util.Set;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.RecordAcademicoRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;

public interface IPersonaDetalleService {

	List<Personadetalle> listarTodosPersonaDetalle();

	public Personadetalle insertarPersonaDetalle(Personadetalle data);

	public Personadetalle actualizarPersonaDetalle(Personadetalle data);

	public Personadetalle buscarPorId(int id);

	public Personadetalle buscarPorIdentificacion(String identificacion);

	public boolean eliminar(Personadetalle data);

	public Personadetalle buscarEmail(String email);

	String verificarDuplicados(String email, String celular, String identificacion);

	String verificarCorreo(String email);

	String verificarTelefono(String celular);

	Personadetalle buscarPorCelular(String celular);

	Personadetalle guardarInformacionCompleta(PersonaDetalleRequest persona, UsuarioDetalleRequest usuario,
			List<RecordAcademicoRequest> recordAcademico) throws Exception;

	Set<Personadetalle> listarAdministradores();

	Set<Personadetalle> listarPaseadores();

	Set<Personadetalle> listarClientes();

	Set<Personadetalle> listarPersonas();
}
