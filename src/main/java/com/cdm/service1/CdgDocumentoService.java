package com.cdm.service1;

import java.util.List;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;

public interface CdgDocumentoService {
	
	List<ResponseCdgDocumentoVO> getCdgDocumentos(String usuario, String sede, String fecInicio, String fecFin, String parametro);
	
	List<ResponseCdgDocumentoVO> getCdgDocumentosUrgentes();
	
	ResponseCdgDocumentoVO getCdgDocumento(Integer id);
	
}
