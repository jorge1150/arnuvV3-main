package com.core.arnuv.request;

import org.hibernate.annotations.Comment;

import com.core.arnuv.enums.EstadoAcademico;
import com.core.arnuv.enums.NivelAcademico;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.RecordAcademico;
import com.core.arnuv.utils.RequestComun;

import lombok.Data;

@Data
public class RecordAcademicoRequest extends RequestComun<RecordAcademico>{

	@Comment("id de record academico")
	private Integer id;

	@Comment("Nivel academico")
    private NivelAcademico nivelAcademico;

	@Comment("Institucion")
    private String institution;

	@Comment("anio graduacion")
    private Integer anioGraduacion;

	@Comment("estado academico")
    private EstadoAcademico estadoAcademico; 
    
	@Comment("Nombre de la persona")
    private Personadetalle persona;
    
	@Comment("Nombre de la persona")
    private Boolean estaEliminado;
}
