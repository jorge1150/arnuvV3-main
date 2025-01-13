package com.core.arnuv.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionCabeceraResponse {
	
	private float latitud;
	private float longitud;
	private int idpersona;
	private List<UbicacionDetalleResponse>  ubicacionDetalleResponse;

}
