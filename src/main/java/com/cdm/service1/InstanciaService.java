package com.cdm.service1;

import java.util.List;

import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseSedeVO;

public interface InstanciaService {
	
	List<ResponseInstanciaVO> getInstanciasSalvaguardia(String sede);
	
	List<ResponseInstanciaVO> getInstanciasAudiencia(String sede, String especialidad);
	
	ResponseInstanciaVO getInstancia(String id);
	
	List<ResponseInstanciaVO> getInstancias(String sede);
	
}
