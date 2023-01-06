package com.cdm.service1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.UsuarioExterno;
import com.cdm.domain.vo.ResponseUsuarioExternoVO;
import com.cdm.mapper.UsuarioExternoMapperService;
import com.cdm.repository.UsuarioExternoRepository;
import com.cdm.service1.UsuarioExternoService;

@Service
public class UsuarioExternoServiceImpl implements UsuarioExternoService {
	
	@Autowired
	private UsuarioExternoRepository usuarioExternoRepository;
	
	@Autowired
	private UsuarioExternoMapperService usuarioExternoMapperService;
	
	@Override
	public List<ResponseUsuarioExternoVO> getUsuarios(String parametro) {
		List<UsuarioExterno> usuarioExternoS = usuarioExternoRepository.findByNombresContainingOrApellidosContaining(parametro, parametro);
		return usuarioExternoMapperService.convertirAVO(usuarioExternoS);
	}

	@Override
	public ResponseUsuarioExternoVO getUsuario(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}
