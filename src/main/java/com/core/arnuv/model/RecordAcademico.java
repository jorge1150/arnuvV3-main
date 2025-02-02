package com.core.arnuv.model;

import com.core.arnuv.enums.EstadoAcademico;
import com.core.arnuv.enums.NivelAcademico;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class RecordAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAcademico nivelAcademico;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = true)
    private Integer anioGraduacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAcademico estadoAcademico; 
    
    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Personadetalle persona;
    
    @Column(nullable = false)
    private Boolean estaEliminado;

    @PrePersist
    public void prePersist() {
        if (estaEliminado == null) {
            estaEliminado = false;
        }
    }
}
