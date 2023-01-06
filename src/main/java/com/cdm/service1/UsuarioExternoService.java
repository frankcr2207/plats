package com.cdm.service1;

import java.util.List;

import com.cdm.domain.vo.ResponseUsuarioExternoVO;

public interface UsuarioExternoService {
	
	public List<ResponseUsuarioExternoVO> getUsuarios(String parametro);
	public ResponseUsuarioExternoVO getUsuario(Integer id);
	
}
