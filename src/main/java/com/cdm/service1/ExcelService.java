package com.cdm.service1;

import java.util.List;

import com.cdm.service.external.vo.ResponseAudienciaActaVO;

public interface ExcelService {

	void exportarExcelActas(List<ResponseAudienciaActaVO> responseAudienciaActaVOS);
	
}
