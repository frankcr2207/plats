package com.cdm.service2;

import java.util.List;

import com.cdm.domain2.FechaHora;
import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;

public interface AuxilioJudicialService {
	List<ResponseAuxilioJudicialVO> getAuxiliosJudiciales(String usuario, String inicio, String fin);

	ResponseAuxilioJudicialVO getAuxilioJudicial(String usuario, Integer id);
	
	ResponseArchivoVO descargarArchivoAuxJud(Integer tipo, Integer id);
	
	void updateAuxilioJudicial(RequestAuxilioJudicialVO requestAuxilioJudicialVO);
	
	void liberarAuxilioJudicial(RequestAuxilioJudicialVO requestAuxilioJudicialVO);
	
	FechaHora getFechaActual();
}
