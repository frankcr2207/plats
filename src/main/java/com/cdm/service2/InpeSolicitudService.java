package com.cdm.service2;

import java.util.List;

import com.cdm.domain2.vo.ResponseInpeSolicitudVO;
import com.cdm.domain2.FechaHora;
import com.cdm.domain2.vo.RequestInpeSolicitudVO;
import com.cdm.domain2.vo.ResponseArchivoVO;

public interface InpeSolicitudService {

	List<ResponseInpeSolicitudVO> getInpeSolicitudes(String inicio, String fin, String usuario);
	
	ResponseInpeSolicitudVO getInpeSolicitud(String usuario, Integer id);
	
	FechaHora getFechaHoraActual();
	
	String descargarArchivoInpeSolicitud(Integer tipo, Integer id);
	
	void liberarInpeSolicitud(RequestInpeSolicitudVO requestInpeSolicitudVO);
	
	String updateInpeSolicitud(RequestInpeSolicitudVO requestInpeSolicitudVO) throws Exception;
}
