package com.cdm.mapper;

import com.cdm.domain.CdgTipoDocumento;
import com.cdm.domain.vo.ResponseCdgTipoDocumentoVO;

public interface CdgTipoDocumentoMapperService {

	ResponseCdgTipoDocumentoVO convertir_a_VO(CdgTipoDocumento cdgTipoDocumento);
	
}
