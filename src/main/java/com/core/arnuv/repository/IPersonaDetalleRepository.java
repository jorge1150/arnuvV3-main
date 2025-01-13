package com.core.arnuv.repository;

import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.Personadetalle;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IPersonaDetalleRepository extends JpaRepository<Personadetalle, Integer> {
    @Query("Select t from Personadetalle t where t.identificacion= ?1")
    Personadetalle buscarPorIdentificacion(String identificacion);
    @Query("Select t from Personadetalle t where t.email= ?1")
    Personadetalle buscarEmail(String email);
    Personadetalle findByCelular(String celular);
}
