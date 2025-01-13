package com.core.arnuv.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.MascotaDetalle;

@Repository
public interface IMascotaDetalleRepository extends JpaRepository<MascotaDetalle, Integer>{
	
	public List<MascotaDetalle> findByIdpersonaId(int idpersona);
	
	public MascotaDetalle findByIdmascota(long idmascota); 

}
