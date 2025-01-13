package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String url;
    private String orden;
    private String icono;

    @ManyToMany
    @JoinTable(name = "menu_item_role",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Rol> roles;

    @OneToMany(mappedBy = "parentMenuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MenuItem> subMenuItems;

    @ManyToOne
    @JoinColumn(name = "parent_menu_item_id")
    private MenuItem parentMenuItem;
}