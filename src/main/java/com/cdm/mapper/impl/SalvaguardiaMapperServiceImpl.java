package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain2.Salvaguardia;
import com.cdm.domain2.vo.ResponseSalvaguardiaVO;
import com.cdm.mapper.SalvaguardiaMapperService;

@Service
public class SalvaguardiaMapperServiceImpl implements SalvaguardiaMapperService{
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseSalvaguardiaVO> convertir_a_VO(List<Salvaguardia> salvaguardias) {
		return modelMapper.map(salvaguardias, new TypeToken<List<ResponseSalvaguardiaVO>>(){}.getType());
	}

	@Override
	public ResponseSalvaguardiaVO convertir_a_VO(Salvaguardia salvaguardia) {
		return modelMapper.map(salvaguardia, ResponseSalvaguardiaVO.class);
	}

}
