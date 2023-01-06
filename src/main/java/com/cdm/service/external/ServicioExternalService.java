package com.cdm.service.external;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.service.external.vo.ResponseAudienciaAgendaExternalVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;

public interface ServicioExternalService {

	public List<ResponseResumenAsistenteVO> getConteoActasSij(String sede, String fechaInicio, String fechaFin, boolean estado);
	
	public boolean getActaSij(String sede, LocalDateTime descargo, String documento);
	
	public List<ResponseAudienciaAgendaExternalVO> getAgendaSij(String sede, List<String> instancias, String fecha);
	
}
