package com.cdm.service1;

import java.util.List;

import com.cdm.domain.vo.ResponseSedeVO;

public interface SedeService {
	
	ResponseSedeVO getSede(String id);
	
	List<ResponseSedeVO> getSedes();
	
	List<ResponseSedeVO> getSedeAudiencia();
	
}
