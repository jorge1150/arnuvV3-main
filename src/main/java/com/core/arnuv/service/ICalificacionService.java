package com.core.arnuv.service;

import java.util.List;

import com.core.arnuv.model.Calificacion;

public interface ICalificacionService {
	
	public List<Calificacion> listarCalificacion();
	public List<Calificacion> BuscarPersonaPasedor(int idpersonapasedor );
	public Calificacion insertarCalificacion(Calificacion data);
	public Calificacion actualizarCalificacion(Calificacion data);
	public Calificacion buscarCalificacion(int id);
	public Calificacion findByIdpaseoId(int idpaseo);	
	public void eliminarCalificacion(int id);
	

}
