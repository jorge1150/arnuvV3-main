package com.core.arnuv.services.imp;

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Paseo;
import com.core.arnuv.repository.IPaseoRepository;
import com.core.arnuv.service.IPaseoService;

@Service
@Component
@RequiredArgsConstructor
public class PaseoServiceImp implements IPaseoService {
	private final IPaseoRepository repo;

	@Override
	public List<Paseo> listarPaseos() {
		return repo.findAll();
	}

	@Override
	public Paseo insertarPaseo(Paseo data) {
		return repo.save(data);
	}

	@Override
	public Paseo actualizarPaseo(Paseo data) {
		return repo.save(data);
	}

	@Override
	public Paseo buscarPorId(int codigo) {
		return repo.findById(codigo).get();
	}
	
	@Override
	public void eliminarPaseo(int codigo) {
		try {
			repo.deleteById(codigo);
		} catch (Exception e) {
			System.out.println("Error al eliminar item");
		}
	}

	@Override
	public List<Paseo> buscarpersonacliente(int idcliente) {
		try {
			return repo.buscarpersonacliente( idcliente);
		} catch (Exception e) {
			System.out.print(e);
			return null;
		}
	}

	@Override
	public List<Paseo> buscaridpersonapasedor(int idpersonapasedor) {
		try {
			return repo.buscaridpersonapasedor( idpersonapasedor);
		} catch (Exception e) {
			System.out.print(e);
			return null;
		}
	}

	@Override
	public List<Paseo> buscarRangoFechasPaseador(Date fechaIni, Date fechaFin, int idpersonapasedor) {
		try {
			return repo.buscarRangoFechasPaseador( fechaIni,fechaFin,idpersonapasedor);
		} catch (Exception e) {
			System.out.print(e);
			return null;
		}
	}
	
	@Override
	public List<Paseo> buscarRangoFechasCliente(Date fechaIni, Date fechaFin, int idpersonacliente) {
		try {
			return repo.buscarRangoFechasCliente( fechaIni,fechaFin,idpersonacliente);
		} catch (Exception e) {
			System.out.print(e);
			return null;
		}
	}



	
	
}
