package com.core.arnuv.service;

import java.util.List;


import com.core.arnuv.model.RecordAcademico;

public interface IRecordAcademicoService {
	 List<RecordAcademico> buscarPorPersonaId(int personaId);

	public RecordAcademico guardarRecordAcademico(RecordAcademico recordAcademico);

	public RecordAcademico buscarRecordAcademicoPorId(Integer id);

	public void eliminarRecordAcademico(int id);
}
