package com.core.arnuv.controller.rest;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.UbicacionCabeceraResponse;
import com.core.arnuv.response.UbicacionDetalleResponse;
import com.core.arnuv.service.*;
import com.core.arnuv.utils.ArnuvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.core.arnuv.constants.Constants.KEY_RADIO;

@RestController
@RequestMapping("/paseos")
@RequiredArgsConstructor
public class PaseoController {
	public final IPersonaDetalleService personaDetalleService;
	public final IUbicacionService ubicacionService;
	public final IParametroService parametroService;

	@GetMapping("/listar/{idpersona}")
	public ResponseEntity<?> listar(@PathVariable(value = "idpersona") Integer personId) {
		Personadetalle personalogueada = personaDetalleService.buscarPorId(personId);		
		var ubicacionPersonaLogueada = ubicacionService.ubicacionPersonaPorDefecto(personalogueada.getId());		
		UbicacionCabeceraResponse ubiCliente = new UbicacionCabeceraResponse();
		ubiCliente.setIdpersona(ubicacionPersonaLogueada.getIdpersona().getId());
		ubiCliente.setLatitud(ubicacionPersonaLogueada.getLatitud());
		ubiCliente.setLongitud(ubicacionPersonaLogueada.getLongitud());
		double radio = parametroService.getParametro(KEY_RADIO).getValorNumber();
		List<UbicacionDetalleResponse> ubiPaseadores = new ArrayList<>();
		
		var listaUbicaciones = ubicacionService.ubicacionPaseadores();
		
		for (Ubicacion ubicacion : listaUbicaciones) {
			double distancia = ArnuvUtils.distance(ubicacionPersonaLogueada.getLatitud(), ubicacionPersonaLogueada.getLongitud(),
					ubicacion.getLatitud(), ubicacion.getLongitud());
			if (distancia <= radio) {
				UbicacionDetalleResponse ubicacionresponce= new UbicacionDetalleResponse();
				ubicacionresponce.setIdpersona(ubicacion.getIdpersona().getId());
				ubicacionresponce.setLatitud(ubicacion.getLatitud());
				ubicacionresponce.setLongitud(ubicacion.getLongitud());
				ubiPaseadores.add(ubicacionresponce);
			}
		}
		ubiCliente.setUbicacionDetalleResponse(ubiPaseadores);
		return ResponseEntity.ok(ubiCliente);
	}
}