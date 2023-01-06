package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.ResponseUsuarioInternoVO;
import com.cdm.mapper.UsuarioExternoMapperService;
import com.cdm.mapper.UsuarioInternoMapperService;

@Service
public class UsuarioInternoMapperServiceImpl implements UsuarioInternoMapperService {
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseUsuarioInternoVO> convertirAVO(List<UsuarioInterno> usuarioInternos) {
		return modelMapper.map(usuarioInternos, new TypeToken<List<ResponseUsuarioInternoVO>>(){}.getType());
	}

	@Override
	public ResponseUsuarioInternoVO convertirAVO(UsuarioInterno usuarioInternos) {
		return modelMapper.map(usuarioInternos, ResponseUsuarioInternoVO.class);
	}

}
