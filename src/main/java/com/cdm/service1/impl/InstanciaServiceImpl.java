package com.cdm.service1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.Instancia;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.mapper.InstanciaMapperService;
import com.cdm.mapper.SedeMapperService;
import com.cdm.repository.InstanciaRepository;
import com.cdm.service1.InstanciaService;

@Service
public class InstanciaServiceImpl implements InstanciaService {
 
	private InstanciaRepository instanciaRepository;
	
	private InstanciaMapperService instanciaMapperService;
	
	private SedeMapperService sedeMapperService;
	
	public InstanciaServiceImpl(InstanciaRepository instanciaRepository, InstanciaMapperService instanciaMapperService,
		SedeMapperService sedeMapperService) {
		this.instanciaMapperService = instanciaMapperService;
		this.instanciaRepository = instanciaRepository;
		this.sedeMapperService = sedeMapperService;
	}
	
	@Override
	public List<ResponseInstanciaVO> getInstanciasSalvaguardia(String sede) {
		List<Instancia> instancias = instanciaRepository.findBySedeIdAndSalvaguardiaIsNotNull(sede);
		return instanciaMapperService.convertir_a_VO(instancias);
	}

	@Override
	public ResponseInstanciaVO getInstancia(String id) {
		Instancia instancia = instanciaRepository.findById(id).get();
		return instanciaMapperService.convertir_a_VO(instancia);
	}

	@Override
	public List<ResponseInstanciaVO> getInstancias(String sede) {
		List<Instancia> instancias = instanciaRepository.findBySedeId(sede);
		return instanciaMapperService.convertir_a_VO(instancias);
	}

}
