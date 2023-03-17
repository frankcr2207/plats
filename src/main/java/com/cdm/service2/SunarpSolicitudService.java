package com.cdm.service2;

import java.util.List;

import com.cdm.domain2.SunarpSolicitud;
import com.cdm.domain2.vo.ResponseSunarpSolicitudVO;

public interface SunarpSolicitudService {
	
	List<ResponseSunarpSolicitudVO> getSunarpSolicitudes(String sede);
	
	ResponseSunarpSolicitudVO getSunarpSolicitud(Integer id);
	
}
