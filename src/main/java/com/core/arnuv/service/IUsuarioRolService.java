package com.core.arnuv.service;

import com.core.arnuv.model.Usuariorol;

import java.util.List;
import java.util.Set;

public interface IUsuarioRolService {

	List<Usuariorol> listarTodosUsuariosRol();

	public Usuariorol insertarUsuarioRol(Usuariorol data);

	public Usuariorol buscarPorId(int idrol, int idusuario);

	Set<Usuariorol> buscarPorRol(int idrol);

	public boolean eliminar(Usuariorol data);

	Set<Usuariorol> buscarPorRolName(String rolName);
	
	public Usuariorol  buscarIdUsuario(int idusuario);
}
