package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.RcRegla;
import com.cdm.domain.vo.ResponseRcReglaVO;
import com.cdm.mapper.RcReglaMapperService;

@Service
public class RcReglaMapperServiceImpl implements RcReglaMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseRcReglaVO> convert_a_VO(List<RcRegla> reglas) {
		return modelMapper.map(reglas, new TypeToken<List<ResponseRcReglaVO>>(){}.getType());
	}

}
