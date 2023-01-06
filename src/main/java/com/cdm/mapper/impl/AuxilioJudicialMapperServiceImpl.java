package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain2.AuxilioJudicial;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.mapper.AuxilioJudicialMapperService;

@Service
public class AuxilioJudicialMapperServiceImpl implements AuxilioJudicialMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseAuxilioJudicialVO> convertirAVO(List<AuxilioJudicial> auxilioJudicialS) {
		return modelMapper.map(auxilioJudicialS, new TypeToken<List<ResponseAuxilioJudicialVO>>(){}.getType());
	}

	@Override
	public ResponseAuxilioJudicialVO convertirAVO(AuxilioJudicial auxilioJudicial) {
		return modelMapper.map(auxilioJudicial, ResponseAuxilioJudicialVO.class);
	}

}
