package com.core.arnuv.service;

import java.util.List;

import com.core.arnuv.utils.EnumUtils;

public interface IEnumOptionService {
	List<EnumUtils.EnumOption> getTamanoPerroOptions();
    List<EnumUtils.EnumOption> getNivelAcademicoOptions();
    List<EnumUtils.EnumOption> getEstadoAcademicoOptions();
}
