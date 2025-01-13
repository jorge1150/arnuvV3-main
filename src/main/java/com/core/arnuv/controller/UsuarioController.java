package com.core.arnuv.controller;

import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.utils.ArnuvNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final IPersonaDetalleService servicioPersonaDetalle;
    private final IUsuarioDetalleService usuarioDetalleService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("crear/{personaId}")
    public String personCreate(Model model, @PathVariable("personaId") Integer personaId) {
        UsuarioDetalleRequest requestUser = new UsuarioDetalleRequest();
        requestUser.setIdpersona(personaId);
        model.addAttribute("nuevo", requestUser);
        return "content-page/usuario-crear";
    }

    @PostMapping("create-access")
    private String personCreateAccess(@ModelAttribute("nuevo") UsuarioDetalleRequest usuario) {
        var personaentity = servicioPersonaDetalle.buscarPorId(usuario.getIdpersona());
        Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
        usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuariodetalle.setIdpersona(personaentity);
        usuariodetalle.setEstado(true);
        Usuariodetalle entity;
        try {
            entity = usuarioDetalleService.insertarUsuarioDetalle(usuariodetalle);
        } catch (DataIntegrityViolationException e) {
            throw new ArnuvNotFoundException("Error al guardar datos: {0}", e.getMessage().split("Detail:")[1].split("]")[0]);
        }
        return "redirect:/role/anclar-rol/".concat(entity.getIdusuario().toString());
    }
}