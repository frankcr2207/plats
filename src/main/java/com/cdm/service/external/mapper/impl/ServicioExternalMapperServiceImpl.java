package com.cdm.service.external.mapper.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.cdm.service.external.mapper.ServicioExternalMapperService;
import com.cdm.service.external.vo.ResponseAudienciaActaExternalVO;
import com.cdm.service.external.vo.ResponseAudienciaActaVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteExternalVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;

@Service
public class ServicioExternalMapperServiceImpl implements ServicioExternalMapperService {

	private static final ModelMapper modelMapper = new ModelMapper();

	@Override
	public List<ResponseAudienciaActaVO> convertir_a_VO_actas(
			List<ResponseAudienciaActaExternalVO> responseAudienciaActaExternalVO) {
		return modelMapper.map(responseAudienciaActaExternalVO, new TypeToken<List<ResponseAudienciaActaVO>>(){}.getType());
	}

	@Override
	public List<ResponseResumenAsistenteVO> convertir_a_VO_resumen(
			List<ResponseResumenAsistenteExternalVO> responseResumenAsistenteExternalVO) {
		return modelMapper.map(responseResumenAsistenteExternalVO, new TypeToken<List<ResponseResumenAsistenteVO>>(){}.getType());
	}

}
