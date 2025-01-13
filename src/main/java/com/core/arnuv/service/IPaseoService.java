package com.core.arnuv.service;

import java.util.Date;
import java.util.List;

import com.core.arnuv.model.Paseo;

public interface IPaseoService {
	
	List<Paseo> listarPaseos();

	public Paseo insertarPaseo(Paseo data);
	
	public Paseo actualizarPaseo(Paseo data);

	public Paseo buscarPorId(int idPaseo);
	
	public void eliminarPaseo(int idPaseo);
	
	List<Paseo> buscarpersonacliente(int idcliente);
	List<Paseo> buscaridpersonapasedor(int idpersonapasedor);
	
	List<Paseo> buscarRangoFechasPaseador(Date fechaIni,Date fechaFin ,int idpersonapasedor);
	
	List<Paseo> buscarRangoFechasCliente(Date fechaIni,Date fechaFin ,int idpersonacliente);
}
