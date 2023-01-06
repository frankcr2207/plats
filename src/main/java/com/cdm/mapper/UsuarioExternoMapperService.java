package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.UsuarioExterno;
import com.cdm.domain.vo.ResponseUsuarioExternoVO;

public interface UsuarioExternoMapperService {
	List<ResponseUsuarioExternoVO> convertirAVO(List<UsuarioExterno> usuarioExternos);
	ResponseUsuarioExternoVO convertirAVO(UsuarioExterno usuarioExterno);
}
