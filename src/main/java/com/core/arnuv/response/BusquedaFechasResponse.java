package com.core.arnuv.response;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.core.arnuv.utils.RespuestaComun;

import lombok.Data;

@Data
public class BusquedaFechasResponse  extends RespuestaComun<BusquedaFechasResponse.BusquedaFechaeDto> {

	@Data
	public static class BusquedaFechaeDto {
	
		@DateTimeFormat(pattern="yyyy-MM-dd")
	    private Date fechaInicial;
		
		@DateTimeFormat(pattern="yyyy-MM-dd")
	    private Date fechaFin;
	}
	

}
