package com.core.arnuv.service;

import com.core.arnuv.model.Ubicacion;

import java.util.List;
import java.util.Set;

public interface IUbicacionService {
	List<Ubicacion> listarUbicacion();

	Ubicacion insertarUbicacion(Ubicacion data);

	Ubicacion actualizarUbicacion(Ubicacion data);

	Ubicacion buscarPorId(int idUbicacion);

	void eliminarUbicacion(int idUbicacion);

	Set<Ubicacion> ubicacionPaseadores();

	Ubicacion ubicacionPersonaPorDefecto(int idpersona);

	Set<Ubicacion> findUbicacionesEnRangoKmPaseadores(double latitud, double longitud, double rangoKm);
}
