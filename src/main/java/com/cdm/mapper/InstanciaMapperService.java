package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.Instancia;
import com.cdm.domain.vo.ResponseInstanciaVO;

public interface InstanciaMapperService {
	
	List<ResponseInstanciaVO> convertir_a_VO(List<Instancia> instancias);
	
	ResponseInstanciaVO convertir_a_VO(Instancia instancia);
	
}
