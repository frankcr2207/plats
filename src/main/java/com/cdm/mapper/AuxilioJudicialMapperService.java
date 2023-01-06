package com.cdm.mapper;

import java.util.List;

import com.cdm.domain2.AuxilioJudicial;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;

public interface AuxilioJudicialMapperService {
	List<ResponseAuxilioJudicialVO> convertirAVO(List<AuxilioJudicial> auxilioJudicialS);
	ResponseAuxilioJudicialVO convertirAVO(AuxilioJudicial auxilioJudicial);
}
