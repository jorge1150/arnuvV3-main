package com.core.arnuv.services.imp;

import com.core.arnuv.model.Usuariosession;
import com.core.arnuv.repository.IUsuarioSesionRepository;
import com.core.arnuv.service.IUsuarioSesionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class UsuarioSesionServiceImp implements IUsuarioSesionService {
	private final IUsuarioSesionRepository repo;

	@Override
	public List<Usuariosession> listarUsuariosSesionActivos() {
		return repo.buscarPorEstado(true);
	}

	@Override
	public List<Usuariosession> listarUsuariosSesionInactivos() {
		return repo.buscarPorEstado(false);
	}

	@Override
	public List<Usuariosession> listarTodosUsuariosSesion() {
		return repo.findAll();
	}

	@Override
	public Usuariosession insertarUsuarioSesion(Usuariosession data) {
		return repo.save(data);
	}

	@Override
	public Usuariosession actualizarUsuarioSesion(Usuariosession data) {
		Usuariosession existeUsuarioSesion = repo.findById(data.getIdUsuario()).orElse(null);
		existeUsuarioSesion.setNumeroIntentos(data.getNumeroIntentos());
		existeUsuarioSesion.setIdSession(data.getIdSession());
		existeUsuarioSesion.setFechaInicio(data.getFechaInicio());
		existeUsuarioSesion.setFechaSalida(data.getFechaSalida());
		existeUsuarioSesion.setActivo(data.getActivo());
		existeUsuarioSesion.setEstado(data.getEstado());
		existeUsuarioSesion.setUserAgent(data.getUserAgent());
		existeUsuarioSesion.setIpTermialRemoto(data.getIpTermialRemoto());
		existeUsuarioSesion.setUsuarioDetalle(data.getUsuarioDetalle());
		return repo.save(existeUsuarioSesion);
	}

	@Override
	public Usuariosession buscarPorId(int id) {
		return repo.findById(id).orElse(null);
	}

}
