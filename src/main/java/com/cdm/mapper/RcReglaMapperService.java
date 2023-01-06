package com.cdm.mapper;

import java.util.List;

import com.cdm.domain.RcRegla;
import com.cdm.domain.vo.ResponseRcReglaVO;

public interface RcReglaMapperService {
	
	List<ResponseRcReglaVO> convert_a_VO(List<RcRegla> reglas);
	
}
