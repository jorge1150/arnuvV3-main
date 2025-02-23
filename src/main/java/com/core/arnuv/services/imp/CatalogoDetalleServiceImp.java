package com.core.arnuv.services.imp;

import com.core.arnuv.model.CatalogoDetalle;
import com.core.arnuv.repository.ICatalogoDetalleRepository;
import com.core.arnuv.service.ICatalogoDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class CatalogoDetalleServiceImp implements ICatalogoDetalleService {
	private final ICatalogoDetalleRepository repo;

	@Override
	public List<CatalogoDetalle> listarCatalogoDetalle() {
		return repo.findAll();
	}

	@Override
	public CatalogoDetalle insertarCatalogoDetalle(CatalogoDetalle data) {
		return repo.save(data);
	}

	@Override
	public CatalogoDetalle actualizarCatalogoDetalle(CatalogoDetalle data) {
		return repo.save(data);
	}

	@Override
	public CatalogoDetalle buscarCatalogoDetalleId(int codigo) {
		return repo.findById(codigo).get();
	}
	
	@Override
	public boolean eliminarCatalogoDetalle(int codigo) {
		try {
			repo.deleteById(codigo);
			return true;
		} catch (Exception e) {
			System.out.println("Error al eliminar item");
			return false;
		}
	}
}
