package com.core.arnuv.services.imp;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Rol;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.repository.IRolRepository;
import com.core.arnuv.repository.IUsuarioRolRepository;
import com.core.arnuv.service.IRolService;

@Service
@Component
@RequiredArgsConstructor
public class RolServiceImp implements IRolService {
	private final IRolRepository repo;
	private final IUsuarioRolRepository repoUsuarioRol;

	@Override
	public List<Rol> listarRolesActivos() {
		return repo.buscarPorEstado(true);
	}

	@Override
	public List<Rol> listarRolesInactivos() {
		return repo.buscarPorEstado(false);
	}

	@Override
	public List<Rol> listarTodosRoles() {
		return repo.findAll();
	}

	@Override
	public Rol insertarRol(Rol data) {
		return repo.save(data);
	}

	@Override
	public Rol actualizarRol(Rol data) {
		Rol existeRol = repo.findById(data.getId()).orElse(null);
		existeRol.setIdpolitica(data.getIdpolitica());
		existeRol.setNombre(data.getNombre());
		existeRol.setActivo(data.getActivo());
		Boolean continueUpdating = true;
		if (!data.getActivo()) {
			Set<Usuariorol> listaReferencias = repoUsuarioRol.buscarPorRol(data.getId());
			if (!listaReferencias.isEmpty()) {
				continueUpdating = false;
			}
		}
		if (continueUpdating) {			
			return repo.save(existeRol);
		} else {
			return null;
		}
	}

	@Override
	public Rol buscarPorId(int id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public Rol findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return repo.findByNombre(nombre);
	}

	
}
