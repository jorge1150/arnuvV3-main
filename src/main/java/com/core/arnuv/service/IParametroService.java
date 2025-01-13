package com.core.arnuv.service;

import java.io.IOException;

import com.core.arnuv.model.Parametros;

public interface IParametroService {
    Parametros getParametro(String code);

    Parametros save(Parametros parametro)throws IOException;

    Parametros delete(Parametros parametro);
    
    Parametros findByCodigo(String code);
}
