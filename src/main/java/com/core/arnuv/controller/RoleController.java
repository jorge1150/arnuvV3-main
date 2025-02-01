package com.core.arnuv.controller;

import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.model.UsuariorolId;
import com.core.arnuv.request.UsuarioRolRequest;
import com.core.arnuv.service.IRolService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final IRolService servicioRol;
    private final IUsuarioDetalleService servicioUsuarioDetalle;
    private final IUsuarioRolService servicioUsuarioRol;

    @GetMapping("/listar")
    public String getRoles(Model model) {
        model.addAttribute("roles", servicioRol.listarTodosRoles());
        return "content-page/roles";
    }

    @GetMapping("/anclar-rol/{userId}")
    public String crearRolUsuario(Model model, @PathVariable("userId") Integer userId) {
        model.addAttribute("roles", servicioRol.listarTodosRoles());
        UsuarioRolRequest nuevo = new UsuarioRolRequest();
        nuevo.setIdusuario(userId);
        nuevo.setUsername(servicioUsuarioDetalle.buscarPorId(userId).getUsername());
        model.addAttribute("nuevo", nuevo);
        return "content-page/rol-crear";
    }

    @PostMapping("/insertar")
    public String nuevo(@ModelAttribute("nuevo") UsuarioRolRequest nuevo) {
        UsuariorolId usuariorolId = new UsuariorolId();
        usuariorolId.setIdusuario(nuevo.getIdusuario());
        usuariorolId.setIdrol(nuevo.getIdrol());
        var rolentity = servicioRol.buscarPorId(nuevo.getIdrol());
        var usuariodetalleentity = servicioUsuarioDetalle.buscarPorId(nuevo.getIdusuario());
        var usuariorolentity = nuevo.mapearDato(nuevo, Usuariorol.class, "idrol","idusuario");
        usuariorolentity.setIdrol(rolentity);
        usuariorolentity.setIdusuario(usuariodetalleentity);
        usuariorolentity.setId(usuariorolId);
        servicioUsuarioRol.insertarUsuarioRol(usuariorolentity);
        return "redirect:/home";
    }
}
