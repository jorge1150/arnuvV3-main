package com.core.arnuv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.Calificacion;

@Repository
public interface ICalificacionRepository  extends JpaRepository<Calificacion, Integer> {
	
	 @Query("Select c from Calificacion c where c.idpaseo.idpersonapasedor.id= ?1")
	List<Calificacion> BuscarPersonaPasedor(int idpersonapasedor);
	
	 //@Query("Select c from Calificacion c where c.idpaseo.idpersonapasedor.id= ?1")
	Calificacion findByIdpaseoId(int idpaseo);
}
