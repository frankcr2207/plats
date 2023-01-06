package com.cdm.service2;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain2.FechaHora;
import com.cdm.domain2.Salvaguardia;
import com.cdm.domain2.vo.RequestSalvaguardiaVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseSalvaguardiaVO;

public interface SalvaguardiaService {
	
	List<ResponseSalvaguardiaVO> getSalvaguardias(String usuario, String inicio, String fin);
	
	ResponseSalvaguardiaVO getSalvaguardia(String usuario, Integer id);
	
	ResponseArchivoVO descargarArchivoSalvaguardia(Integer tipo, Integer id);
	
	void updateSalvaguardia(RequestSalvaguardiaVO requestSalvaguardiaVO) throws Exception;
	
	void liberarSalvaguardia(RequestSalvaguardiaVO requestSalvaguardiaVO);
	
	FechaHora getFechaHoraActual();
	
	ResponseArchivoVO descargarArchivoGeneralSalvaguardia(Salvaguardia salvaguardia, String archivoFinal, Integer id) throws Exception;
}
