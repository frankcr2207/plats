package com.cdm.service1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.Especialidad;
import com.cdm.repository.EspecialidadRepository;
import com.cdm.service1.EspecialidadService;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {
	
	@Autowired
	private EspecialidadRepository especialidadRepository;
	
	@Override
	public List<Especialidad> getEspecialidades() {
		return especialidadRepository.findAll();
	}

}
