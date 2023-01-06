package com.cdm.mapper.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cdm.domain.RcExpediente;
import com.cdm.domain.vo.ResponseRcExpedienteVO;
import com.cdm.mapper.RcExpedienteMapperService;

@Service
public class RcExpedienteMapperServiceImpl implements RcExpedienteMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public ResponseRcExpedienteVO convert_a_VO(RcExpediente rcExpediente) {
		return modelMapper.map(rcExpediente, ResponseRcExpedienteVO.class);
	}

}
