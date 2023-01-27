package com.cdm.service.external.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cdm.service.external.ServicioExternalService;
import com.cdm.service.external.mapper.ServicioExternalMapperService;
import com.cdm.service.external.vo.ResponseAudienciaAgendaExternalVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteExternalVO;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;

import com.cdm.utils.Constantes;

@Service
public class ServicioExternalServiceImpl implements ServicioExternalService {
	
	@Value("${endpoint.project.servicio}")
	private String END_POINT_SERVICIO;
	
	@Value("${endpoint.project.servicio.getConteoActasSij}")
	private String METODO_CONTEO_ACTAS;
	
	@Value("${endpoint.project.servicio.getAgendaSij}")
	private String METODO_AGENDA_AUDIENCIAS;
	
	@Value("${endpoint.project.servicio.getActaSij}")
	private String METODO_DESCARGAR_ACTA;
	
	private ServicioExternalMapperService servicioExternalMapperService;
	
	public ServicioExternalServiceImpl(ServicioExternalMapperService servicioExternalMapperService) {
		this.servicioExternalMapperService = servicioExternalMapperService;
	} 
	
	private Integer orden = 0;
	
	@Override
	public List<ResponseResumenAsistenteVO> getConteoActasSij(String sede, String fechaInicio, String fechaFin, String estado) {
		RestTemplate restTemplate = new RestTemplate();
		List<ResponseResumenAsistenteExternalVO> responseResumenAsistenteExternalVO = new ArrayList<>();
		String url = END_POINT_SERVICIO + METODO_CONTEO_ACTAS + "?sede=" + sede + "&fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin + "&estado=" + estado;
		responseResumenAsistenteExternalVO = Arrays.asList(restTemplate.getForObject(url, ResponseResumenAsistenteExternalVO[].class));
		List<ResponseResumenAsistenteVO> responseResumenAsistenteVOS = servicioExternalMapperService.convertir_a_VO_resumen(responseResumenAsistenteExternalVO);
		responseResumenAsistenteVOS.stream().forEach(r -> {
			orden = 0;
			r.getAudienciaActas().stream().forEach(a -> {
				orden++;
				a.setOrden(orden);
				a.setFecDescargoCorto(a.getFecDescargo());
			});
		});
		return responseResumenAsistenteVOS;
	}
	
	@Override
	public Boolean getActaSij(String sede, LocalDateTime descargo, String documento) {
		RestTemplate restTemplate = new RestTemplate();
		String url = END_POINT_SERVICIO + METODO_DESCARGAR_ACTA + "?sede=" + sede + "&descargo=" + descargo + "&documento=" + documento;
		return restTemplate.getForObject(url, Boolean.class);
	}

	@Override
	public List<ResponseAudienciaAgendaExternalVO> getAgendaSij(String sede, List<String> instancias, String fecha) {
		RestTemplate restTemplate = new RestTemplate();
		List<ResponseAudienciaAgendaExternalVO> responseResumenAsistenteExternalVO = new ArrayList<>();
		String url = END_POINT_SERVICIO + METODO_AGENDA_AUDIENCIAS + "?sede=" + sede + "&instancias=" + instancias.toString()
			.replace("[", "").replace("]", "") + "&fecha=" + fecha;
		responseResumenAsistenteExternalVO = Arrays.asList(restTemplate.getForObject(url, ResponseAudienciaAgendaExternalVO[].class));
		responseResumenAsistenteExternalVO.stream().forEach(r -> {
			r.setHora(r.getFechaHora());
			r.setEnlace(getEnlace(r.getEnlace().toLowerCase().trim()));
			if(r.getEnlace().length() != 0) {
				String ver = r.getEnlace().substring(0, 8);
				if(!ver.equals(Constantes.PROTOCOLO_WEB)) {
					r.setEnlace(Constantes.PROTOCOLO_WEB.concat(r.getEnlace()));
				}
			}
		});
		return responseResumenAsistenteExternalVO;
	}
	
	public String getEnlace(String enlace) {
    	String link = "";
    	String inicio = Constantes.DOMINIO_MEET;
    	int inicioPos = enlace.indexOf(inicio);
        if(inicioPos != -1)
        	link = enlace.substring(inicioPos, enlace.length());
        else
        	link = "";
    	return link;
    }

}
