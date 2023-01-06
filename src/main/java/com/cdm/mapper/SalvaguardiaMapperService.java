package com.cdm.mapper;

import java.util.List;

import com.cdm.domain2.Salvaguardia;
import com.cdm.domain2.vo.ResponseSalvaguardiaVO;

public interface SalvaguardiaMapperService {
	
	List<ResponseSalvaguardiaVO> convertir_a_VO(List<Salvaguardia> salvaguardias);
	ResponseSalvaguardiaVO convertir_a_VO(Salvaguardia salvaguardia);
	
}
