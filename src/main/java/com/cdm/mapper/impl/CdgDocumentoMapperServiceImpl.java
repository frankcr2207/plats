package com.cdm.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.mapper.CdgDocumentoMapperService;

@Service
public class CdgDocumentoMapperServiceImpl implements CdgDocumentoMapperService {
	
	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public List<ResponseCdgDocumentoVO> convertir_a_VO(List<CdgDocumento> cdgDocumentos) {
		return modelMapper.map(cdgDocumentos, new TypeToken<List<ResponseCdgDocumentoVO>>(){}.getType());
	}

	@Override
	public ResponseCdgDocumentoVO convertir_a_VO(CdgDocumento cdgDocumento) {
		return modelMapper.map(cdgDocumento, ResponseCdgDocumentoVO.class);
	}

}
