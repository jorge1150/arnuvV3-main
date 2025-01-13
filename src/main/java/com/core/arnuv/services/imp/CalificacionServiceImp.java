package com.core.arnuv.services.imp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Calificacion;
import com.core.arnuv.repository.ICalificacionRepository;
import com.core.arnuv.service.ICalificacionService;

@Service
@Component
@RequiredArgsConstructor
public class CalificacionServiceImp implements ICalificacionService {
	private final ICalificacionRepository repo;
	@Override
	public List<Calificacion> listarCalificacion() {
		return repo.findAll();
	}

	@Override
	public List<Calificacion> BuscarPersonaPasedor(int idpersonapasedor) {
		return repo.BuscarPersonaPasedor(idpersonapasedor);
	}

	@Override
	public Calificacion insertarCalificacion(Calificacion data) {
		return repo.save(data);
	}

	@Override
	public Calificacion actualizarCalificacion(Calificacion data) {
		return repo.save(data);
	}

	@Override
	public Calificacion buscarCalificacion(int id) {
		return repo.findById(id).get();
	}

	@Override
	public void eliminarCalificacion(int id) {
		try {
			repo.deleteById(id);
		} catch (Exception e) {
			System.out.println("Error al eliminar item");
		}
	}

	@Override
	public Calificacion findByIdpaseoId(int idpaseo) {
		return repo.findByIdpaseoId(idpaseo);
	}
}
