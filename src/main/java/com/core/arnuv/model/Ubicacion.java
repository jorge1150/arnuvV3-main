package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
@Data
@Entity
@Table(name = "ubicacion")
public class Ubicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUbicacion;
    private float latitud;
    private float longitud;
    private String descripcion;
    private Integer isDefault;

    @ManyToOne()
    @Comment("Codigo de personaDetalle")
    @JoinColumn(name = "idpersona")
    @ToString.Exclude
    private Personadetalle idpersona;
}
