package com.cdm.service1.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cdm.domain.SedeUsuario;
import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.ResponseUsuarioInternoVO;
import com.cdm.mapper.UsuarioInternoMapperService;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service1.UsuarioInternoService;
import com.cdm.utils.Constantes;

@Service
public class UsuarioInternoServiceImpl implements UsuarioInternoService {
	
	private UsuarioInternoMapperService usuarioInternoMapperService;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	public UsuarioInternoServiceImpl(UsuarioInternoMapperService usuarioInternoMapperService, UsuarioInternoRepository usuarioInternoRepository,
		SedeUsuarioRepository sedeUsuarioRepository){
		this.usuarioInternoMapperService = usuarioInternoMapperService;
		this.usuarioInternoRepository = usuarioInternoRepository;
		this.sedeUsuarioRepository = sedeUsuarioRepository;
	}
	
	@Override
	public ResponseUsuarioInternoVO getUsuarioInterno(String id) {
		UsuarioInterno usuarioInterno = this.usuarioInternoRepository.findById(id).get();
		return this.usuarioInternoMapperService.convertirAVO(usuarioInterno);
	}

	@Override
	public List<ResponseUsuarioInternoVO> getUsuariosBySede(String sede) {
		List<SedeUsuario> sedeUsuarios = this.sedeUsuarioRepository.findBySede(sede);
		List<String> idUsuarios = sedeUsuarios.stream().map(SedeUsuario::getUsuario).distinct().collect(Collectors.toList());
		List<UsuarioInterno> usuarioInternos = this.usuarioInternoRepository.findByUsuarioInAndPerfil(idUsuarios, Constantes.USUARIO_PERFIL_ASISTENTE_CDM_CDG);
		return this.usuarioInternoMapperService.convertirAVO(usuarioInternos);
	}

}
