package com.cdm.mapper.impl;

import org.modelmapper.ModelMapper;

import com.cdm.domain.CdgTipoDocumento;
import com.cdm.domain.vo.ResponseCdgTipoDocumentoVO;
import com.cdm.mapper.CdgTipoDocumentoMapperService;

public class CdgTipoDocumentoMapperServiceImpl implements CdgTipoDocumentoMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public ResponseCdgTipoDocumentoVO convertir_a_VO(CdgTipoDocumento cdgTipoDocumento) {
		return modelMapper.map(cdgTipoDocumento, ResponseCdgTipoDocumentoVO.class);
	}

}
