package com.core.arnuv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.RecordAcademico;

@Repository
public interface IRecordAcademicoRepository  extends JpaRepository<RecordAcademico, Integer>{
	  @Query("SELECT r FROM RecordAcademico r WHERE r.personadetalle.id = :personId")
	    List<RecordAcademico> buscarPorPersonaId(@Param("personaId") int personId);
}
