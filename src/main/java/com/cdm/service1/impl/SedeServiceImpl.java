package com.cdm.service1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.mapper.SedeMapperService;
import com.cdm.repository.SedeRepository;
import com.cdm.service1.SedeService;

@Service
public class SedeServiceImpl implements SedeService{
	
	
	private SedeRepository sedeRepository;
	
	private SedeMapperService sedeMapperService;
	
	public SedeServiceImpl(SedeRepository sedeRepository, SedeMapperService sedeMapperService) {
		this.sedeRepository = sedeRepository;
		this.sedeMapperService = sedeMapperService;
	}
	
	@Override
	public ResponseSedeVO getSede(String id) {
		Sede sede = sedeRepository.findById(id).get();
		return sedeMapperService.convertir_a_VO(sede);
	}

	@Override
	public List<ResponseSedeVO> getSedes() {
		List<Sede> sedes = sedeRepository.findAll();
		return sedeMapperService.convertir_a_VO(sedes);
	}

}
