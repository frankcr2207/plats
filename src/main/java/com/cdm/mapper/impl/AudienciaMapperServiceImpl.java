package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.cdm.domain.Audiencia;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.mapper.AudienciaMapperService;

@Service
public class AudienciaMapperServiceImpl implements AudienciaMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<Audiencia> convertir_a_entidad(List<RequestAudienciaVO> requestAudiencia) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(requestAudiencia, new TypeToken<List<Audiencia>>(){}.getType());
	}

	@Override
	public Audiencia convertir_a_entidad(RequestAudienciaVO requestAudiencia) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(requestAudiencia, Audiencia.class);
	}

	@Override
	public List<ResponseAudienciaVO> convertir_a_VO(List<Audiencia> audiencia) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(audiencia, new TypeToken<List<ResponseAudienciaVO>>(){}.getType());
	}

	@Override
	public ResponseAudienciaVO convertir_a_VO(Audiencia audiencia) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(audiencia, ResponseAudienciaVO.class);
	}

}
