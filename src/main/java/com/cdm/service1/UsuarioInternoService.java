package com.cdm.service1;

import java.util.List;

import com.cdm.domain.vo.ResponseUsuarioInternoVO;

public interface UsuarioInternoService {
	
	ResponseUsuarioInternoVO getUsuarioInterno(String id);
	
	List<ResponseUsuarioInternoVO> getUsuariosBySede(String sede);
	
}
