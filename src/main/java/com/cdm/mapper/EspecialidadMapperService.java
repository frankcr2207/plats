package com.cdm.mapper;

import com.cdm.domain.Especialidad;
import com.cdm.domain.vo.ResponseEspecialidadVO;

public interface EspecialidadMapperService {
	
	ResponseEspecialidadVO convertir_a_VO(Especialidad especialidad);
	
}
