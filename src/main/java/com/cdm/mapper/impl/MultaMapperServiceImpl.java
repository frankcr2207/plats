package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.Multa;
import com.cdm.domain.vo.RequestNuevaMultaVO;
import com.cdm.domain.vo.ResponseMultaVO;
import com.cdm.mapper.MultaMapperService;

@Service
public class MultaMapperServiceImpl implements MultaMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseMultaVO> convertir_a_VO(List<Multa> multas) {
		return modelMapper.map(multas, new TypeToken<List<ResponseMultaVO>>(){}.getType());
	}

	@Override
	public ResponseMultaVO convertir_a_VO(Multa multa) {
		return modelMapper.map(multa, ResponseMultaVO.class);
	}

	@Override
	public Multa convertir_a_Entidad(RequestNuevaMultaVO requestNuevaMultaVO) {
		return modelMapper.map(requestNuevaMultaVO, Multa.class);
	}

}
