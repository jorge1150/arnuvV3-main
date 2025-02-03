package com.core.arnuv.services.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.RecordAcademico;
import com.core.arnuv.repository.IRecordAcademicoRepository;
import com.core.arnuv.service.IRecordAcademicoService;

@Service
public class RecordAcademicoImp implements IRecordAcademicoService {
	  @Autowired
	    private IRecordAcademicoRepository recordAcademicoRepository;
	  @Override
		public List<RecordAcademico> buscarPorPersonaId(int personaId) {
			// TODO Auto-generated method stub
			return recordAcademicoRepository.buscarPorPersonaId(personaId);
		}
	@Override
	public RecordAcademico guardarRecordAcademico(RecordAcademico recordAcademico) {		
		return recordAcademicoRepository.save(recordAcademico);
	}
	@Override
	public RecordAcademico buscarRecordAcademicoPorId(Integer id) {
		
		return recordAcademicoRepository.findById(id).orElse(null);
	}
	@Override
	public void eliminarRecordAcademico(int id) {
		RecordAcademico recordAcademico=this.buscarRecordAcademicoPorId(id);
		if(recordAcademico!=null) {
			recordAcademico.setEstaEliminado(true);
			this.guardarRecordAcademico(recordAcademico);
		}
		
	}


}
