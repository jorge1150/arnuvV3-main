package com.core.arnuv.service;

import java.util.List;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;

public interface IPersonaDetalleService {

	List<Personadetalle> listarTodosPersonaDetalle();

	public Personadetalle insertarPersonaDetalle(Personadetalle data);

	public Personadetalle actualizarPersonaDetalle(Personadetalle data);

	public Personadetalle buscarPorId(int id);

	public Personadetalle buscarPorIdentificacion(String identificacion);

	public boolean eliminar(Personadetalle data);
   public  Personadetalle buscarEmail(String email);

	String verificarDuplicados(String email, String celular, String identificacion);

	Personadetalle buscarPorCelular(String celular);

	Personadetalle guardarInformacionCompleta(PersonaDetalleRequest persona, UsuarioDetalleRequest usuario) throws Exception;

}
