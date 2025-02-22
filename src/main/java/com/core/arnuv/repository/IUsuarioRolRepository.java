package com.core.arnuv.repository;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.model.UsuariorolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IUsuarioRolRepository extends JpaRepository<Usuariorol, UsuariorolId> {

    @Query("SELECT u FROM Usuariorol u WHERE u.id.idrol =?1 and u.id.idusuario =?2")
    Usuariorol buscarbyid(int idrol, int idusuario);

    @Query("SELECT u FROM Usuariorol u WHERE u.id.idrol =?1")
    Set<Usuariorol> buscarPorRol(int idrol);

    @Query("SELECT ur.idusuario.idpersona FROM Usuariorol ur WHERE ur.idrol.nombre = ?1")
    Set<Personadetalle> getAllByRolePerson(String rolName);
    
    @Query("SELECT u FROM Usuariorol u WHERE u.idusuario.idusuario =?1")
    Usuariorol buscarIdUsuario(int idusuario);
}