package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain2.RecPartida;
import com.cdm.domain2.vo.ResponseRecPartidaVO;
import com.cdm.mapper.RecPartidaMapperService;

@Service
public class RecPartidaMapperServiceImpl implements RecPartidaMapperService{

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseRecPartidaVO> convertirAVO(List<RecPartida> recPartidaS) {
		return modelMapper.map(recPartidaS, new TypeToken<List<ResponseRecPartidaVO>>(){}.getType());
	}

	@Override
	public ResponseRecPartidaVO convertirAVO(RecPartida recPartida) {
		return modelMapper.map(recPartida, ResponseRecPartidaVO.class);
	}

}
