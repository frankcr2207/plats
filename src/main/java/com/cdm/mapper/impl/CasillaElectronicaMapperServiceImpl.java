package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.CasillaElectronica;
import com.cdm.domain.vo.ResponseCasillaElectronicaVO;
import com.cdm.mapper.CasillaElectronicaMapperService;

@Service
public class CasillaElectronicaMapperServiceImpl implements CasillaElectronicaMapperService {
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseCasillaElectronicaVO> convertirAVO(List<CasillaElectronica> casillaElectronicaS) {
		return modelMapper.map(casillaElectronicaS, new TypeToken<List<ResponseCasillaElectronicaVO>>(){}.getType());
	}

	@Override
	public ResponseCasillaElectronicaVO convertirAVO(CasillaElectronica casillaElectronica) {
		return modelMapper.map(casillaElectronica, ResponseCasillaElectronicaVO.class);
	}

}
