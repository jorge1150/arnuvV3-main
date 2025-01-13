package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.Instant;

@Data
@Comment("Tabla que almacena los tokens de un usuario")
@Entity
@Table(name = "token_usuario")
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(name = "token")
    private String token;

    @Column(name = "fecha_inicio")
    private Instant startDate;

    @Column(name = "fecha_fin")
    private Instant endDate;

    @Column(name = "estado")
    private Boolean estado;
}
