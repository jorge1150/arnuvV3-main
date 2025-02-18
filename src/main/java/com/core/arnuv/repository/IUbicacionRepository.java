package com.core.arnuv.repository;

import com.core.arnuv.model.Ubicacion;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {
	@Query("SELECT u FROM Ubicacion u WHERE u.idpersona.id=?2 AND u.isDefault=?1 ")
	Optional<Ubicacion> findByIsDefaultAndIdpersona(int isDefault, int idPersona);

	@Query("SELECT u FROM Ubicacion u WHERE u.isDefault=?2 AND u.idpersona.id in ?1")
	Set<Ubicacion> getAllByListPersonId(Set<Integer> personId, Integer isDefault);

	@Query("SELECT u FROM Ubicacion u WHERE " + "(6371 * acos(cos(radians(:latitud)) * cos(radians(u.latitud)) "
			+ "* cos(radians(u.longitud) - radians(:longitud)) "
			+ "+ sin(radians(:latitud)) * sin(radians(u.latitud)))) <= :rangoKm AND u.idpersona.id in :idPersona")
	Set<Ubicacion> findUbicacionesEnRangoKm(@Param("idPersona") Set<Integer> idPersona,
			@Param("latitud") double latitud, @Param("longitud") double longitud, @Param("rangoKm") double rangoKm);

}
