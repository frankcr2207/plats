package com.cdm.service2.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.domain.SedeUsuario;
import com.cdm.domain2.vo.ResponseInpeSolicitudVO;
import com.cdm.domain2.FechaHora;
import com.cdm.domain2.InpeArchivo;
import com.cdm.domain2.InpeSolicitud;
import com.cdm.domain2.Salvaguardia;
import com.cdm.domain2.SalvaguardiaArchivo;
import com.cdm.domain2.vo.RequestInpeSolicitudVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.mapper.InpeSolicitudMapperService;
import com.cdm.repository.InstanciaRepository;
import com.cdm.repository.SedeRepository;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository2.InpeArchivoRepository;
import com.cdm.repository2.InpeSolicitudRepository;
import com.cdm.service1.FtpCdgService;
import com.cdm.service2.FtpModuloServiceDos;
import com.cdm.service2.InpeSolicitudService;
import com.cdm.utils.Utilitario;

@Service
public class InpeSolicitudServiceImpl implements InpeSolicitudService {
	
	private static final Integer modulo = 8;
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	@Autowired
	private InpeSolicitudRepository inpeSolicitudRepository;
	
	@Autowired
	private InpeArchivoRepository inpeArchivoRepository;
	
	@Autowired
	private SedeRepository sedeRepository;
	
	@Autowired
	private InstanciaRepository instanciaRepository;
	
	@Autowired
	private InpeSolicitudMapperService inpeSolicitudMapperService;
	
	@Autowired
	private FtpModuloServiceDos ftpModuloService;
	
	@Autowired
	private FtpCdgService ftpCdgService;
	
	@Override
	public List<ResponseInpeSolicitudVO> getInpeSolicitudes(String inicio, String fin, String usuario) {
		List<SedeUsuario> sedesUsuario = this.sedeUsuarioRepository.findByUsuario(usuario);		
		List<String> sedes = sedesUsuario.stream().map(SedeUsuario::getSede).distinct().collect(Collectors.toList());
		List<InpeSolicitud> inpeSolicitudes = new ArrayList<>();

		if(Objects.nonNull(inicio) && Objects.nonNull(fin)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime fechaInicio = LocalDateTime.parse(inicio, formatter);
			LocalDateTime fechaFin = LocalDateTime.parse(fin, formatter);
			inpeSolicitudes = this.inpeSolicitudRepository.findByFechaSistemaBetweenAndSedeIn(fechaInicio, fechaFin, sedes);
		}
		else {
			List<String> estados = Arrays.asList("P","T");
			inpeSolicitudes = this.inpeSolicitudRepository.findBySedeInAndEstadoIn(sedes, estados);
		}
		
		return inpeSolicitudMapperService.convertir_a_VO(inpeSolicitudes);
	}

	@Override
	public ResponseInpeSolicitudVO getInpeSolicitud(String usuario, Integer id) {
		InpeSolicitud inpeSolicitud = inpeSolicitudRepository.findById(id).get();
		ResponseInpeSolicitudVO responseInpeSolicitudVO = this.inpeSolicitudMapperService.convertir_a_VO(inpeSolicitud);
		responseInpeSolicitudVO.setInstancia(this.instanciaRepository.findById(responseInpeSolicitudVO.getInstancia()).get().getDenominacion());
		responseInpeSolicitudVO.setSede(this.sedeRepository.findById(responseInpeSolicitudVO.getSede()).get().getDenominacion());
		if(inpeSolicitud.getEstado().equals("P")) {
			inpeSolicitud.setEstado("T");
			inpeSolicitud.setUsuario(usuario);
			InpeSolicitud inpeSolicitudAux = inpeSolicitudRepository.saveAndFlush(inpeSolicitud);
			ResponseInpeSolicitudVO responseInpeSolicitudVOAux = this.inpeSolicitudMapperService.convertir_a_VO(inpeSolicitudAux);
			responseInpeSolicitudVOAux.setInstancia(responseInpeSolicitudVO.getInstancia());
			responseInpeSolicitudVOAux.setSede(responseInpeSolicitudVO.getSede());
			return responseInpeSolicitudVOAux;
		}
		else {
			return responseInpeSolicitudVO;
		}
			
	}

	@Override
	public FechaHora getFechaHoraActual() {
		return inpeSolicitudRepository.getFechaHoraActual();
	}

	@Override
	public String descargarArchivoInpeSolicitud(Integer tipo, Integer id) {
		String carpeta, nombre;
		
		if(tipo == 1) {
			InpeSolicitud inpeSolicitud = inpeSolicitudRepository.findById(id).get();
			carpeta = inpeSolicitud.getCarpeta();
			nombre = inpeSolicitud.getArchivo();
		}
		else {
			InpeArchivo archivoSalvaguradia = inpeArchivoRepository.findById(id).get();
			carpeta = archivoSalvaguradia.getCarpeta();
			nombre = archivoSalvaguradia.getArchivo();
		}

		if(ftpModuloService.conectarFTP(modulo)) {
			Boolean estado = ftpModuloService.descargarArchivo(carpeta, nombre);
			if(estado == null)
				throw new RuntimeException("No se pudo descargar el archivo del FTP.");
			else if(!estado)
				throw new RuntimeException("Archivo no fue cargado correctamente por el usuario.");
			else if(estado)
				return nombre;
		}
		else
			throw new RuntimeException("No se pudo conectar al servidor FTP");
		
		return nombre;
	}

	@Override
	public void liberarInpeSolicitud(RequestInpeSolicitudVO requestInpeSolicitudVO) {
		InpeSolicitud inpeSolicitud = inpeSolicitudRepository.findById(requestInpeSolicitudVO.getIdSolicitud()).get();
		inpeSolicitud.setEstado("P");
		inpeSolicitud.setUsuario(null);
		inpeSolicitudRepository.save(inpeSolicitud);
	}

	@Transactional
	@Override
	public String updateInpeSolicitud(RequestInpeSolicitudVO requestInpeSolicitudVO) throws Exception {
		LocalDateTime ahora = inpeSolicitudRepository.getFechaHoraActual().getFechaHora();
		
		InpeSolicitud inpeSolicitud = inpeSolicitudRepository.findById(requestInpeSolicitudVO.getIdSolicitud()).get();
		String archivoFinal = "";
		
		if(inpeSolicitud.getEstado().equals("A"))
			throw new RuntimeException("El registro ya fue atendido, actualice la lista.");
		
		if(requestInpeSolicitudVO.getTipoRespuesta().equals("A")) {
			Integer entero = Integer.parseInt(requestInpeSolicitudVO.getCodigo());
			
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
			
			archivoFinal = String.format("%010d", entero) + "-" + requestInpeSolicitudVO.getAnio()
				+ "-ESC-" + requestInpeSolicitudVO.getOrgano() + "-" + requestInpeSolicitudVO.getEspecialidad()  
				+ "-" + ahora.format(formato) + ".pdf";
			
			this.descargarArchivoInpeSolicitud(1, inpeSolicitud.getIdSolicitud());
			
			List<InputStream> listaPdf = new ArrayList<InputStream>();
			listaPdf.add(new FileInputStream(local + inpeSolicitud.getArchivo()));
			for(InpeArchivo inpeArchivo : inpeSolicitud.getInpeArchivos()) {
				this.descargarArchivoInpeSolicitud(2, inpeArchivo.getId());
				listaPdf.add(new FileInputStream(local + inpeArchivo.getArchivo()));
			}
			
			Utilitario.unirPdf(listaPdf, archivoFinal);
            
			if(!ftpCdgService.conectarFTP(inpeSolicitud.getSede()))
				throw new RuntimeException("No se pudo conectar al FTP de la sede");
			
			if(!ftpCdgService.cargarArchivo(archivoFinal))
				throw new RuntimeException("No se pudo enviar el archivo al FTP de la sede");
			
			inpeSolicitud.setCodigo(archivoFinal);
		}
		
		inpeSolicitud.setEstado(requestInpeSolicitudVO.getTipoRespuesta());
		inpeSolicitud.setObservacion(requestInpeSolicitudVO.getTextoRespuesta().toUpperCase());
		inpeSolicitud.setFechaAtencion(ahora);
		inpeSolicitud.setIp(getClientIp());
		inpeSolicitudRepository.save(inpeSolicitud);	
		
		return archivoFinal;
		
	}
	
	private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }

}
