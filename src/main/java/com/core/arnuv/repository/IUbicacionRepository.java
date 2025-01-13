package com.core.arnuv.repository;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;

import java.util.Optional;
import java.util.Set;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {
	@Query("SELECT u FROM Ubicacion u WHERE u.idpersona.id=?2 AND u.isDefault=?1 ")
	Optional<Ubicacion> findByIsDefaultAndIdpersona(int isDefault,int idPersona);

	@Query("SELECT u FROM Ubicacion u WHERE u.isDefault=?2 AND u.idpersona.id in ?1")
	Set<Ubicacion> getAllByListPersonId(Set<Integer> personId, Integer isDefault);
}
