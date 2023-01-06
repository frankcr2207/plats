package com.cdm.mapper;

import java.util.List;

import com.cdm.domain2.AuxilioJudicial;
import com.cdm.domain2.RecPartida;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseRecPartidaVO;

public interface RecPartidaMapperService {
	List<ResponseRecPartidaVO> convertirAVO(List<RecPartida> recPartidaS);
	ResponseRecPartidaVO convertirAVO(RecPartida recPartidaVO);
}
