package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.Audiencia;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;

public interface AudienciaMapperService {
	
	List<Audiencia> convertir_a_entidad(List<RequestAudienciaVO> requestAudiencia);
	
	Audiencia convertir_a_entidad(RequestAudienciaVO requestAudiencia);
	
	List<ResponseAudienciaVO> convertir_a_VO(List<Audiencia> audiencia);
	
	ResponseAudienciaVO convertir_a_VO(Audiencia audiencia);
}
