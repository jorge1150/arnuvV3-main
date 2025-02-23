package com.core.arnuv.service;

import java.io.IOException;
import java.util.List;

import com.core.arnuv.model.MascotaDetalle;

public interface IMascotaDetalleService {

	List<MascotaDetalle> listarMascotasDetalle();

	public MascotaDetalle insertarMascotaDetalle(MascotaDetalle data) throws IOException;
	
	public MascotaDetalle actualizarMascotaDetalle(MascotaDetalle data);

	public MascotaDetalle buscarMascotaID(int codigo);
	
	public  boolean EliminarMascotaDetalle(int codigo);

	List<MascotaDetalle>findByIdpersonaId(int idpersona);
	
	
	public MascotaDetalle findByIdmascota(int idmascota); 
}
