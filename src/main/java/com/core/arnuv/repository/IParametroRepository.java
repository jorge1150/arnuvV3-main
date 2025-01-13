package com.core.arnuv.repository;

import com.core.arnuv.model.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IParametroRepository extends JpaRepository<Parametros, Integer> {
    Optional<Parametros> findByCodigoAndEstado(String codigo, Boolean estado);
    
    Parametros findByCodigo(String codigo);
    
    
}