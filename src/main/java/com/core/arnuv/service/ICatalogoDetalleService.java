package com.core.arnuv.service;

import com.core.arnuv.model.CatalogoDetalle;

import java.util.List;

/**
 * Interfaz de metodos que expone el servicio
 */
public interface ICatalogoDetalleService {

	public List<CatalogoDetalle> listarCatalogoDetalle();

	public CatalogoDetalle insertarCatalogoDetalle(CatalogoDetalle data);

	public CatalogoDetalle actualizarCatalogoDetalle(CatalogoDetalle data);

	public CatalogoDetalle buscarCatalogoDetalleId(int idCatalogo);
	
	public boolean eliminarCatalogoDetalle(int idCataldoDetalle);

}
