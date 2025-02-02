package com.core.arnuv.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.core.arnuv.utils.EnumUtils;

@Repository
public interface IEnumOptionRepository {
	List<EnumUtils.EnumOption> getTamanoPerroOptions();

	List<EnumUtils.EnumOption> getNivelAcademicoOptions();

	List<EnumUtils.EnumOption> getEstadoAcademicoOptions();
}
