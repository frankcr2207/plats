package com.cdm.service.external.mapper;

import java.util.List;

import com.cdm.service.external.vo.ResponseAudienciaActaExternalVO;
import com.cdm.service.external.vo.ResponseAudienciaActaVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteExternalVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;

public interface ServicioExternalMapperService {
	
	public List<ResponseAudienciaActaVO> convertir_a_VO_actas(List<ResponseAudienciaActaExternalVO> responseAudienciaActaExternalVO);
	
	public List<ResponseResumenAsistenteVO> convertir_a_VO_resumen(List<ResponseResumenAsistenteExternalVO> responseResumenAsistenteExternalVO);
	
}
