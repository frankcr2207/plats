package com.cdm.service1.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.SedeUsuario;
import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.mapper.CdgDocumentoMapperService;
import com.cdm.repository.CdgDocumentoRepository;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service1.CdgDocumentoService;

@Service
public class CdgDocumentoServiceImpl implements CdgDocumentoService{
	
	private static final int perfil_jefe_cdg = 15;
	
	private static final int perfil_asistente_cdg = 5;
	
	private static final String estado_cdg_pendiente = "P";
	
	private static final String documento_elevado = "S";
	
	private static final String documento_no_elevado = "N";
	
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	private CdgDocumentoMapperService cdgDocumentoMapperService;
	
	private CdgDocumentoRepository cdgDocumentoRepository;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	public CdgDocumentoServiceImpl(SedeUsuarioRepository sedeUsuarioRepository, CdgDocumentoMapperService cdgDocumentoMapperService,
		CdgDocumentoRepository cdgDocumentoRepository, UsuarioInternoRepository usuarioInternoRepository) {
		this.sedeUsuarioRepository = sedeUsuarioRepository;
		this.cdgDocumentoMapperService = cdgDocumentoMapperService;
		this.cdgDocumentoRepository = cdgDocumentoRepository;
		this.usuarioInternoRepository = usuarioInternoRepository;
	}
	
	@Override
	public List<ResponseCdgDocumentoVO> getCdgDocumentos(String usuario, String sede, String fecInicio, String fecFin, String parametro) {
		UsuarioInterno usuarioInterno = this.usuarioInternoRepository.findById(usuario).get();
		List<CdgDocumento> cdgDocumentos = new ArrayList<>();
		LocalDateTime fechaInicio = null, fechaFin = null;
		
		if(!fecInicio.isEmpty() && !fecFin.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			fechaInicio = LocalDateTime.parse(fecInicio + " 00:00", formatter);
			fechaFin = LocalDateTime.parse(fecFin + " 23:59", formatter);
		}
		
		if(usuarioInterno.getPerfil() == perfil_jefe_cdg) {
			if(!fecInicio.isEmpty() && !fecFin.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findByFecSistemaBetween(fechaInicio, fechaFin);
			else if(!parametro.isEmpty()) 
				cdgDocumentos = this.cdgDocumentoRepository.findByExpedienteContainingOrUsuarioExternoNombresContaining(parametro, parametro);
			else if(!sede.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findBySedeIdAndEstado(sede, "P");
			else
				cdgDocumentos = this.cdgDocumentoRepository.findBySedeIdAndEstado("0401", "P");
		}
		else if(usuarioInterno.getPerfil() == perfil_asistente_cdg){
			if(!fecInicio.isEmpty() && !fecFin.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findByUsuarioInternoAndFecSistemaBetween(usuario, fechaInicio, fechaFin);
			else if(!parametro.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findByUsuarioInternoAndExpedienteContainingOrUsuarioExternoNombresContaining(usuario, parametro, parametro);
			else
				cdgDocumentos = this.cdgDocumentoRepository.findByUsuarioInternoAndEstadoAndSuperior(usuario, estado_cdg_pendiente, documento_no_elevado);
		}
		return cdgDocumentoMapperService.convertir_a_VO(cdgDocumentos);
	}

	@Override
	public ResponseCdgDocumentoVO getCdgDocumento(Integer id) {
		CdgDocumento cdgDocumento = this.cdgDocumentoRepository.findById(id).get();
		return cdgDocumentoMapperService.convertir_a_VO(cdgDocumento);
	}

	@Override
	public List<ResponseCdgDocumentoVO> getCdgDocumentosUrgentes() {
		List<CdgDocumento> cdgDocumentos = this.cdgDocumentoRepository.findBySuperiorAndEstado(documento_elevado, estado_cdg_pendiente);
		return cdgDocumentoMapperService.convertir_a_VO(cdgDocumentos);
	}

}
