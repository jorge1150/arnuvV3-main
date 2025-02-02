package com.core.arnuv.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.arnuv.model.RecordAcademico;
import com.core.arnuv.service.IRecordAcademicoService;

@RestController
@RequestMapping("/records-academicos")
public class RecordAcademicoRestController {
	@Autowired
    private IRecordAcademicoService recordAcademicoService;

    
	 @GetMapping("/{id}")
    public List<RecordAcademico> listarTodos(@PathVariable int id) {
        return recordAcademicoService.buscarPorPersonaId(id);
    }

    @GetMapping("/buscar/{id}")
    public RecordAcademico obtenerPorId(@PathVariable int id) {
        return recordAcademicoService.buscarRecordAcademicoPorId(id);
    }

    @PostMapping
    public RecordAcademico guardar(@RequestBody RecordAcademico record) {
        return recordAcademicoService.guardarRecordAcademico(record);
    }

  
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        recordAcademicoService.eliminarRecordAcademico(id);
    }

    
    @GetMapping("/persona/{personId}")
    public List<RecordAcademico> obtenerPorPersona(@PathVariable int personId) {
        return recordAcademicoService.buscarPorPersonaId(personId);
    }
}
