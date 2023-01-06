package com.cdm.service1.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cdm.domain.ArchivoCasillaElectronica;
import com.cdm.domain.CasillaElectronica;
import com.cdm.domain.vo.RequestCasillaElectronicaVO;
import com.cdm.domain.vo.ResponseCasillaElectronicaVO;
import com.cdm.domain.vo.ResponseUsuarioExternoVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.mapper.CasillaElectronicaMapperService;
import com.cdm.repository.ArchivoCasillaElectronicaRepository;
import com.cdm.repository.CasillaElectronicaRepository;
import com.cdm.service1.CasillaElectronicaService;
import com.cdm.service1.FtpModuloService;
import com.cdm.service1.UsuarioExternoService;

@Service
public class CasillaElectronicaServiceImpl implements CasillaElectronicaService {
	
	private static final Integer modulo = 9;
	
	private CasillaElectronicaRepository casillaElectronicaRepository;
	
	private ArchivoCasillaElectronicaRepository archivoCasillaElectronicaRepository;
	
	private CasillaElectronicaMapperService casillaElectronicaMapperService;
	
	private UsuarioExternoService usuarioExternoService;
	
	private FtpModuloService ftpModuloService;
	
	public CasillaElectronicaServiceImpl(CasillaElectronicaRepository casillaElectronicaRepository,  
		ArchivoCasillaElectronicaRepository archivoCasillaElectronicaRepository, 
		CasillaElectronicaMapperService casillaElectronicaMapperService,
		UsuarioExternoService usuarioExternoService,
		FtpModuloService ftpModuloService) {
		this.casillaElectronicaRepository = casillaElectronicaRepository;
		this.archivoCasillaElectronicaRepository = archivoCasillaElectronicaRepository;
		this.casillaElectronicaMapperService = casillaElectronicaMapperService;
		this.usuarioExternoService = usuarioExternoService;
		this.ftpModuloService = ftpModuloService;
	}
	
	@Override
	public List<ResponseCasillaElectronicaVO> getSolicitudes() {
		List<CasillaElectronica> casillaElectronicaS = this.casillaElectronicaRepository.findByEstado("P");
		return casillaElectronicaMapperService.convertirAVO(casillaElectronicaS);
	}

	@Override
	public List<ResponseCasillaElectronicaVO> getSolicitudesPorUsuario(String parametro) {
		List<ResponseUsuarioExternoVO> responseUsuarioExternoVOS = usuarioExternoService.getUsuarios(parametro);
		List<Integer> ids = responseUsuarioExternoVOS.stream().map(ResponseUsuarioExternoVO::getId).collect(Collectors.toList());
		List<CasillaElectronica> casillaElectronicaS = this.casillaElectronicaRepository.findByUsuarioExternoIdIn(ids);
		return casillaElectronicaMapperService.convertirAVO(casillaElectronicaS);
	}

	@Override
	public ResponseCasillaElectronicaVO getSolicitud(Integer id) {
		CasillaElectronica casillaElectronica = this.casillaElectronicaRepository.findById(id).get();
		return casillaElectronicaMapperService.convertirAVO(casillaElectronica);
	}

	@Override
	public String descargarArchivoCasillaElectronica(Integer tipo, Integer id) {
		String carpeta, nombre;

		ArchivoCasillaElectronica archivoCasillaElectronica = archivoCasillaElectronicaRepository.findById(id).orElse(null);
		carpeta = archivoCasillaElectronica.getCarpeta();
		nombre = archivoCasillaElectronica.getNombre();

		ftpModuloService.conectarFTP(modulo);
		ftpModuloService.descargarArchivo(carpeta, nombre);
		
		return nombre;
	}
	
	@Transactional
	@Override
	public void updateCasillaElectronica(RequestCasillaElectronicaVO requestAuxilioJudicialVO) {
		CasillaElectronica casillaElectronica = this.casillaElectronicaRepository.findById(requestAuxilioJudicialVO.getIdCasillaElectronica()).get();
		LocalDateTime horaActual = this.casillaElectronicaRepository.getFechaHoraActual().getFechaHora();
		if(!requestAuxilioJudicialVO.getAdjunto().getOriginalFilename().equals("")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
			String directorio = horaActual.format(formatter);
			String adjuntoFinal = requestAuxilioJudicialVO.getIdCasillaElectronica() + "_respuesta_ce.pdf";
			ftpModuloService.conectarFTP(modulo);
			ftpModuloService.cargarArchivo(adjuntoFinal, directorio, requestAuxilioJudicialVO.getAdjunto());
			casillaElectronica.setAdjuntoRespuesta(adjuntoFinal);
			casillaElectronica.setCarpetaRespuesta(directorio);
		}
		casillaElectronica.setEstado(requestAuxilioJudicialVO.getEstado());
		casillaElectronica.setRespuesta(requestAuxilioJudicialVO.getRespuesta());
		casillaElectronica.setFechaRespuesta(horaActual);
		this.casillaElectronicaRepository.save(casillaElectronica);
	}

}
