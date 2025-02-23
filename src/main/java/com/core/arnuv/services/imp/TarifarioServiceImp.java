package com.core.arnuv.services.imp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Tarifario;
import com.core.arnuv.repository.ITarifarioRapository;
import com.core.arnuv.service.ITarifarioService;

@Service
@Component
@RequiredArgsConstructor
public class TarifarioServiceImp implements ITarifarioService {
	private final ITarifarioRapository repo;

	@Override
	public List<Tarifario> listarTarifarios() {
		return repo.findAll();
	}

	@Override
	public Tarifario insertarTarifario(Tarifario data) {
		return repo.save(data);
	}

	@Override
	public Tarifario actualizarTarifario(Tarifario data) {
		/*
		 * Tarifario existeTarifario = repo.findById(data.getId()).orElse(null);
		 * existeTarifario.setNombre(data.getNombre());
		 * existeTarifario.setTiempo(data.getTiempo());
		 * existeTarifario.setPrecio(data.getPrecio());
		 * existeTarifario.setActivo(data.getActivo());
		 */
		return repo.save(data);
	}

	@Override
	public Tarifario buscarPorId(int codigo) {
		return repo.findById(codigo).get();
	}

	@Override
	public boolean eliminarTarifario(int codigo) {
		try {
			repo.deleteById(codigo);
			return true;
		} catch (Exception e) {
			System.out.println("Error al eliminar item");
			return false;
		}

	}

	@Override
	public List<Tarifario> buscarPorEstado(Boolean estado) {
		return repo.buscarPorEstado(estado);
	}

}
