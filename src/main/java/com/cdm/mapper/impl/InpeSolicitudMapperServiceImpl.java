package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain2.vo.ResponseInpeSolicitudVO;
import com.cdm.domain2.InpeSolicitud;
import com.cdm.mapper.InpeSolicitudMapperService;

@Service
public class InpeSolicitudMapperServiceImpl implements InpeSolicitudMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseInpeSolicitudVO> convertir_a_VO(List<InpeSolicitud> inpeSolicitudes) {
		return modelMapper.map(inpeSolicitudes, new TypeToken<List<ResponseInpeSolicitudVO>>(){}.getType());
	}

	@Override
	public ResponseInpeSolicitudVO convertir_a_VO(InpeSolicitud inpeSolicitud) {
		return modelMapper.map(inpeSolicitud, ResponseInpeSolicitudVO.class);
	}

}
