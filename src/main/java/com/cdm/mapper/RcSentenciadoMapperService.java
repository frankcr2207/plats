package com.cdm.mapper;

import com.cdm.domain.RcSentenciado;
import com.cdm.domain.vo.ResponseRcSentenciadoVO;

public interface RcSentenciadoMapperService {
	
	ResponseRcSentenciadoVO convert_a_VO(RcSentenciado rcSentenciado);
	
}
