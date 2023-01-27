package com.cdm.service1;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.vo.RequestTurnoVO;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.domain.vo.ResponseCdgTurnoVO;
import com.cdm.entities.Turno;

public interface CdgDocumentoService {
	
	List<ResponseCdgDocumentoVO> getCdgDocumentos(String usuario, String sede, String fecInicio, String fecFin, String parametro);
	
	List<ResponseCdgDocumentoVO> getCdgDocumentosUrgentes();
	
	ResponseCdgDocumentoVO getCdgDocumento(Integer id);
	
	void guardarTurnoCdg(RequestTurnoVO turno);
	
	void anularTurnoCdg(Integer idTurno);
	
	List<ResponseCdgTurnoVO> getTurnosCdg(String sede, LocalDateTime inicio, LocalDateTime fin);
	
}
