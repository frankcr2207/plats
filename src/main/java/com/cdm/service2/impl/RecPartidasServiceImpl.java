package com.cdm.service2.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.SedeUsuario;
import com.cdm.domain2.RecPartida;
import com.cdm.domain2.vo.ResponseRecPartidaVO;
import com.cdm.mapper.RecPartidaMapperService;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository2.RecPartidaRepository;
import com.cdm.service2.RecPartidaService;

@Service
public class RecPartidasServiceImpl implements RecPartidaService {
	
	@Autowired
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	@Autowired
	private RecPartidaRepository recPartidaRepository;
	
	@Autowired
	private RecPartidaMapperService recPartidaMapperService;
	
	@Override
	public List<ResponseRecPartidaVO> getRecPartidas(String usuario) {
		List<SedeUsuario> sedeUsuarioS = this.sedeUsuarioRepository.findByUsuario(usuario);		
		List<String> sedes = sedeUsuarioS.stream().map(SedeUsuario::getSede).collect(Collectors.toList());
		List<String> estados = Arrays.asList("P","T");
		List<RecPartida> recPartidaS = this.recPartidaRepository.findByEstadoInAndSedeIn(estados, sedes);
		return recPartidaMapperService.convertirAVO(recPartidaS);
	}

	@Override
	public List<ResponseRecPartidaVO> findRecPartidas(String usuario, LocalDateTime inicio, LocalDateTime fin) {
		List<SedeUsuario> sedeUsuarioS = this.sedeUsuarioRepository.findByUsuario(usuario);		
		List<String> sedes = sedeUsuarioS.stream().map(SedeUsuario::getSede).collect(Collectors.toList());
		List<RecPartida> recPartidaS = this.recPartidaRepository.findByFechaSistemaBetweenAndSedeIn(inicio, fin, sedes);
		return recPartidaMapperService.convertirAVO(recPartidaS);
	}

	@Override
	public ResponseRecPartidaVO getRecPartida(Integer id) {
		RecPartida recPartida = this.recPartidaRepository.getOne(id);
		return recPartidaMapperService.convertirAVO(recPartida);
	}

}
