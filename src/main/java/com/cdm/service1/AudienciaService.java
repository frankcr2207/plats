package com.cdm.service1;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;

public interface AudienciaService {
	
	public List<ResponseAudienciaVO> getAudienciasPublicadas(List<String> instancias, LocalDateTime fecha); 
	
	public void publicarAudiencias(List<RequestAudienciaVO> requestAudienciaVO);
	
	public void modificarAudiencia(RequestAudienciaVO requestAudienciaVO);
	
	public ResponseAudienciaVO getAudiencia(Integer id); 
	
}
