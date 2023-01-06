package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.Multa;
import com.cdm.domain.vo.RequestNuevaMultaVO;
import com.cdm.domain.vo.ResponseMultaVO;

public interface MultaMapperService {

	List<ResponseMultaVO> convertir_a_VO(List<Multa> multas);
	
	ResponseMultaVO convertir_a_VO(Multa multa);
	
	Multa convertir_a_Entidad(RequestNuevaMultaVO requestNuevaMultaVO);
	
}
