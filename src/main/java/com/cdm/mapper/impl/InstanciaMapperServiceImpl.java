package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.Instancia;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.mapper.InstanciaMapperService;

@Service
public class InstanciaMapperServiceImpl implements InstanciaMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseInstanciaVO> convertir_a_VO(List<Instancia> instancias) {
		return modelMapper.map(instancias, new TypeToken<List<ResponseInstanciaVO>>(){}.getType());
	}

	@Override
	public ResponseInstanciaVO convertir_a_VO(Instancia instancia) {
		return modelMapper.map(instancia, ResponseInstanciaVO.class);
	}

}
