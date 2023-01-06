package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.CasillaElectronica;
import com.cdm.domain.vo.ResponseCasillaElectronicaVO;

public interface CasillaElectronicaMapperService {
	List<ResponseCasillaElectronicaVO> convertirAVO(List<CasillaElectronica> casillaElectronicaS);
	ResponseCasillaElectronicaVO convertirAVO(CasillaElectronica casillaElectronica);
}
