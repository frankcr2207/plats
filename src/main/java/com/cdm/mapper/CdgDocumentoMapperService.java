package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;

public interface CdgDocumentoMapperService {
	
	List<ResponseCdgDocumentoVO> convertir_a_VO(List<CdgDocumento> cdgDocumentos);
	
	ResponseCdgDocumentoVO convertir_a_VO(CdgDocumento cdgDocumento);
	
}
