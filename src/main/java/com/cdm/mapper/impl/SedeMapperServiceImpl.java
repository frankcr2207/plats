package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.mapper.SedeMapperService;

@Service
public class SedeMapperServiceImpl implements SedeMapperService{
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseSedeVO> convertir_a_VO(List<Sede> sedes) {
		return modelMapper.map(sedes, new TypeToken<List<ResponseSedeVO>>(){}.getType());
	}

	@Override
	public ResponseSedeVO convertir_a_VO(Sede sede) {
		return modelMapper.map(sede, ResponseSedeVO.class);
	}

	@Override
	public Sede convertir_a_Entidad(ResponseSedeVO responseSedeVO) {
		return modelMapper.map(responseSedeVO, Sede.class);
	}

}
