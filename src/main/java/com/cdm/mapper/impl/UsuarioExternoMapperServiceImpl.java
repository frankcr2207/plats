package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.UsuarioExterno;
import com.cdm.domain.vo.ResponseUsuarioExternoVO;
import com.cdm.mapper.UsuarioExternoMapperService;

@Service
public class UsuarioExternoMapperServiceImpl implements UsuarioExternoMapperService {
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseUsuarioExternoVO> convertirAVO(List<UsuarioExterno> usuarioExternos) {
		return modelMapper.map(usuarioExternos, new TypeToken<List<ResponseUsuarioExternoVO>>(){}.getType());
	}

	@Override
	public ResponseUsuarioExternoVO convertirAVO(UsuarioExterno usuarioExterno) {
		return modelMapper.map(usuarioExterno, ResponseUsuarioExternoVO.class);
	}

}
