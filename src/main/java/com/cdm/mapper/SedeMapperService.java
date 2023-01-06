package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.Instancia;
import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseSedeVO;

public interface SedeMapperService {

	List<ResponseSedeVO> convertir_a_VO(List<Sede> sedes);
	
	ResponseSedeVO convertir_a_VO(Sede sede);
	
	Sede convertir_a_Entidad(ResponseSedeVO responseSedeVO);
	
}
