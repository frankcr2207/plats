package com.cdm.service1;

import java.util.List;

import com.cdm.domain.vo.RequestGestionMultaVO;
import com.cdm.domain.vo.RequestNuevaMultaVO;
import com.cdm.domain.vo.ResponseConsultaDocumento;
import com.cdm.domain.vo.ResponseMultaVO;

public interface MultaService {
	
	List<ResponseMultaVO> getMultasSJ(String parametro);
	
	List<ResponseMultaVO> getMultasSec(String usuario, String parametro);
	
	ResponseMultaVO getMulta(Integer id);
	
	ResponseMultaVO guardarMulta(RequestNuevaMultaVO requestNuevaMultaVO, String usuario);
	
	ResponseMultaVO guardarGestionMulta(RequestGestionMultaVO requestGestionMultaVO, String usuario);
	
	ResponseConsultaDocumento consultaServicios(String servicio, String documento);
	
	void testALertas();
}
