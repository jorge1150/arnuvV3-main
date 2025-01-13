package com.core.arnuv.services.imp;

import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.repository.IUsuarioRolRepository;
import com.core.arnuv.service.IUsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Component
@RequiredArgsConstructor
public class UsuarioRolServiceImp implements IUsuarioRolService {
	private final IUsuarioRolRepository repo;

	@Override
	public List<Usuariorol> listarTodosUsuariosRol() {
		return repo.findAll();
	}

	@Override
	@Transactional
	public Usuariorol insertarUsuarioRol(Usuariorol data) {
		return repo.save(data);
	}

	@Override
	public Usuariorol buscarPorId(int idrol, int idusuario) {
		return repo.buscarbyid(idrol, idusuario);
	}

	@Override
	public Set<Usuariorol> buscarPorRol(int idrol) {
		return repo.buscarPorRol(idrol);
	}

	@Override
	public boolean eliminar(Usuariorol data) {
		repo.delete(data);
		return true;
	}

	@Override
	public Set<Usuariorol> buscarPorRolName(String rolName) {
		return this.buscarPorRolName(rolName);
	}

	@Override
	public Usuariorol buscarIdUsuario(int idusuario) {
		return repo.buscarIdUsuario(idusuario);
	}
}
