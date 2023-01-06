package com.cdm.mapper.impl;

import org.modelmapper.ModelMapper;

import com.cdm.domain.Especialidad;
import com.cdm.domain.vo.ResponseEspecialidadVO;
import com.cdm.mapper.EspecialidadMapperService;

public class EspecialidadMapperServiceImpl implements EspecialidadMapperService{
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public ResponseEspecialidadVO convertir_a_VO(Especialidad especialidad) {
		return modelMapper.map(especialidad, ResponseEspecialidadVO.class);
	}

	
	
}
