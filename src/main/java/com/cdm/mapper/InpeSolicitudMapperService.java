package com.cdm.mapper;

import java.util.List;

import com.cdm.domain2.vo.ResponseInpeSolicitudVO;
import com.cdm.domain2.InpeSolicitud;

public interface InpeSolicitudMapperService {
	
	List<ResponseInpeSolicitudVO> convertir_a_VO(List<InpeSolicitud> inpeSolicitudes);
	
	ResponseInpeSolicitudVO convertir_a_VO(InpeSolicitud inpeSolicitud);
	
}
