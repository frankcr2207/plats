package com.cdm.mapper;

import com.cdm.domain.RcExpediente;
import com.cdm.domain.vo.ResponseRcExpedienteVO;

public interface RcExpedienteMapperService {
	
	ResponseRcExpedienteVO convert_a_VO(RcExpediente rcExpediente);
	
}
