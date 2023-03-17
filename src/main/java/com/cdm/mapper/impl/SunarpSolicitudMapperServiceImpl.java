package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain2.SunarpSolicitud;
import com.cdm.domain2.vo.ResponseSunarpSolicitudVO;
import com.cdm.mapper.SunarpSolicitudMapperService;

@Service
public class SunarpSolicitudMapperServiceImpl implements SunarpSolicitudMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseSunarpSolicitudVO> convertir_a_VO(List<SunarpSolicitud> sunarpSolicitudes) {
		return modelMapper.map(sunarpSolicitudes, new TypeToken<List<ResponseSunarpSolicitudVO>>(){}.getType());
	}

	@Override
	public ResponseSunarpSolicitudVO convertir_a_VO(SunarpSolicitud sunarpSolicitud) {
		return modelMapper.map(sunarpSolicitud, ResponseSunarpSolicitudVO.class);
	}

}
