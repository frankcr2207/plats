package com.cdm.mapper.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cdm.domain.RcSentenciado;
import com.cdm.domain.vo.ResponseRcSentenciadoVO;
import com.cdm.mapper.RcSentenciadoMapperService;

@Service
public class RcSentenciadoMapperServiceImpl implements RcSentenciadoMapperService{
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public ResponseRcSentenciadoVO convert_a_VO(RcSentenciado rcSentenciado) {
		return modelMapper.map(rcSentenciado, ResponseRcSentenciadoVO.class);
	}

}
