package com.core.arnuv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.Tarifario;

@Repository
public interface ITarifarioRapository extends JpaRepository<Tarifario, Integer> {

	@Query("Select r from Tarifario r where r.activo= ?1")
	List<Tarifario> buscarPorEstado(Boolean estado);
}
