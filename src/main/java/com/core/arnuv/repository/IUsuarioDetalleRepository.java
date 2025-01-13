package com.core.arnuv.repository;

import com.core.arnuv.model.Usuariodetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioDetalleRepository extends JpaRepository<Usuariodetalle, Integer> {
    @Query("SELECT u from Usuariodetalle u where u.username = ?1 or u.idpersona.email = ?1")
    Optional<Usuariodetalle> buscarPorEmailOrUsername(String username);

    @Query("Select t from Usuariodetalle t where t.username= ?1")
    Optional<Usuariodetalle> buscarPorUsername(String username);

    @Query("SELECT t from Usuariodetalle t WHERE t.token.token= ?1")
    Usuariodetalle buscarToken(String token);
    
    @Query("SELECT t from Usuariodetalle t WHERE t.idpersona.id= ?1")
    Usuariodetalle buscarpersona(int idpersona);
}
