package com.cdm.service1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.RcExpediente;
import com.cdm.domain.vo.ResponseRcExpedienteVO;
import com.cdm.mapper.RcExpedienteMapperService;
import com.cdm.repository.RcExpedienteRepository;
import com.cdm.service1.RcExpedienteService;

@Service
public class RcExpedienteServiceImpl implements RcExpedienteService{

	@Autowired
	private RcExpedienteRepository rcExpedienteRepository;
	
	@Autowired
	private RcExpedienteMapperService rcExpedienteMapperService;
	
	@Override
	public ResponseRcExpedienteVO getExpediente(Integer id) {
		RcExpediente rcExpediente = this.rcExpedienteRepository.findById(id).get();
		return rcExpedienteMapperService.convert_a_VO(rcExpediente);
	}

}
