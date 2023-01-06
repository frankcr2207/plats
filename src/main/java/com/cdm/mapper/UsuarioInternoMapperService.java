package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.UsuarioExterno;
import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.ResponseUsuarioExternoVO;
import com.cdm.domain.vo.ResponseUsuarioInternoVO;

public interface UsuarioInternoMapperService {
	List<ResponseUsuarioInternoVO> convertirAVO(List<UsuarioInterno> usuarioInternos);
	ResponseUsuarioInternoVO convertirAVO(UsuarioInterno usuarioInterno);
}
