package com.cdm.mapper;

import java.util.List;

import com.cdm.domain2.SunarpSolicitud;
import com.cdm.domain2.vo.ResponseSunarpSolicitudVO;

public interface SunarpSolicitudMapperService {
	
	List<ResponseSunarpSolicitudVO> convertir_a_VO(List<SunarpSolicitud> sunarpSolicitudes);
	
	ResponseSunarpSolicitudVO convertir_a_VO(SunarpSolicitud sunarpSolicitud);
	
}
