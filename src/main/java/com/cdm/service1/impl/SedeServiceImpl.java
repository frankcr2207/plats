package com.cdm.service1.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.Sede;
import com.cdm.domain.SedeUsuario;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.mapper.SedeMapperService;
import com.cdm.repository.SedeRepository;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.service1.SedeService;

@Service
public class SedeServiceImpl implements SedeService{
	
	private SedeRepository sedeRepository;
	
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	private SedeMapperService sedeMapperService;
	
	public SedeServiceImpl(SedeRepository sedeRepository, SedeMapperService sedeMapperService, SedeUsuarioRepository sedeUsuarioRepository) {
		this.sedeRepository = sedeRepository;
		this.sedeMapperService = sedeMapperService;
		this.sedeUsuarioRepository = sedeUsuarioRepository;
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

	@Override
	public List<ResponseSedeVO> getSedeAudiencia() {
		List<Sede> sedes = sedeRepository.findByAudiencia("S");
		return sedeMapperService.convertir_a_VO(sedes);
	}

	@Override
	public List<ResponseSedeVO> getSedesPorUsuario(String usuario) {
		List<SedeUsuario> sedeUsuario = this.sedeUsuarioRepository.findByUsuario(usuario);
		List<String> idSedes = sedeUsuario.stream().map(SedeUsuario::getSede).distinct().collect(Collectors.toList());
		List<Sede> sedes = this.sedeRepository.findByIdIn(idSedes);
		return this.sedeMapperService.convertir_a_VO(sedes);
	}

}
