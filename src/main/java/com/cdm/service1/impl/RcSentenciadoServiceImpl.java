package com.cdm.service1.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.RcSentenciado;
import com.cdm.domain.vo.ResponseRcSentenciadoVO;
import com.cdm.mapper.RcSentenciadoMapperService;
import com.cdm.repository.RcSentenciadoRepository;
import com.cdm.service1.RcSentenciadoService;

@Service
public class RcSentenciadoServiceImpl implements RcSentenciadoService{
	
	@Autowired
	private RcSentenciadoRepository rcSentenciadoRepository;
	
	@Autowired
	private RcSentenciadoMapperService rcSentenciadoMapperService;
	
	@Override
	public ResponseRcSentenciadoVO getSentenciado(String documento) {
		RcSentenciado rcSentenciado = this.rcSentenciadoRepository.findByDocumento(documento);
		ResponseRcSentenciadoVO responseRcSentenciadoVO = new ResponseRcSentenciadoVO();
		if(Objects.isNull(rcSentenciado))
			throw new RuntimeException("No existe registro del documento ingresado.");
		else
			responseRcSentenciadoVO = rcSentenciadoMapperService.convert_a_VO(rcSentenciado);
		return responseRcSentenciadoVO;
	}
	
}
