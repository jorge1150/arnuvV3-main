package com.core.arnuv.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.core.arnuv.enums.EstadoAcademico;
import com.core.arnuv.enums.NivelAcademico;
import com.core.arnuv.enums.TamanoPerroEnum;
import com.core.arnuv.service.IEnumOptionService;
import com.core.arnuv.utils.EnumUtils;
@Service
public class IEnumImpl implements IEnumOptionService {
	@Override
	public List<EnumUtils.EnumOption> getTamanoPerroOptions() {
		return EnumUtils.getEnumOptions(TamanoPerroEnum.class);
	}

	@Override
	public List<EnumUtils.EnumOption> getNivelAcademicoOptions() {
		return EnumUtils.getEnumOptions(NivelAcademico.class);
	}

	@Override
	public List<EnumUtils.EnumOption> getEstadoAcademicoOptions() {
		return EnumUtils.getEnumOptions(EstadoAcademico.class);
	}
}
