package com.core.arnuv.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.Paseo;


@Repository
public interface IPaseoRepository extends JpaRepository<Paseo, Integer> {
	
	@Query("Select p from Paseo p where p.idpersonacliente.id= ?1 order by id desc")
    List<Paseo> buscarpersonacliente(int idcliente);
	
	@Query("Select p from Paseo p where p.idpersonapasedor.id= ?1 order by id desc")
    List<Paseo> buscaridpersonapasedor(int idpersonapasedor);
	
	
	@Query("Select p from Paseo p where cast( p.fecharealinicio as date) between ?1 and ?2  and p.idpersonapasedor.id= ?3 and estado='FINALIZADO' order by id desc")
    List<Paseo> buscarRangoFechasPaseador(Date fechaIni,Date fechaFin ,int idpersonapasedor);
	
	@Query("Select p from Paseo p where cast( p.fecharealinicio as date) between ?1 and ?2  and p.idpersonacliente.id= ?3 and estado='FINALIZADO' order by id desc")
    List<Paseo> buscarRangoFechasCliente(Date fechaIni,Date fechaFin ,int idpersonacliente);

	
}
