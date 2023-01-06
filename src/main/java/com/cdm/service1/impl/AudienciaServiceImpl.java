package com.cdm.service1.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cdm.domain.Audiencia;
import com.cdm.domain.Instancia;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;
import com.cdm.mapper.AudienciaMapperService;
import com.cdm.repository.AudienciaRepository;
import com.cdm.repository.InstanciaRepository;
import com.cdm.service1.AudienciaService;
import com.cdm.utils.Constantes;
import com.cdm.utils.UtilIP;

@Service
public class AudienciaServiceImpl implements AudienciaService {
	
	private InstanciaRepository instanciaRepository;
	
	private AudienciaRepository audienciaRepository;
	
	private AudienciaMapperService audienciaMapperService;
	
	public AudienciaServiceImpl(AudienciaRepository audienciaRepository,
			AudienciaMapperService audienciaMapperService, InstanciaRepository instanciaRepository) {
		this.audienciaRepository = audienciaRepository;
		this.audienciaMapperService = audienciaMapperService;
		this.instanciaRepository = instanciaRepository;
	}
	
	@Override
	public void publicarAudiencias(List<RequestAudienciaVO> requestAudienciaVO) {
		UtilIP utilIP = new UtilIP();
		LocalDateTime ahora = this.audienciaRepository.getFechaHoraActual().getFechaHora();
		List<Audiencia> audiencias = new ArrayList<>();
		requestAudienciaVO.stream().forEach(r -> {
			Optional<Audiencia> audiencia = this.audienciaRepository.findByExpedienteAndIdInstanciaAndEspecialidadAndFecAudiencia(
					r.getExpediente(), r.getIdInstancia(), r.getEspecialidad(), r.getFecAudiencia());
			if(!audiencia.isPresent()) {
				audiencias.add(this.audienciaMapperService.convertir_a_entidad(r));
			}
		});
		audiencias.stream().forEach(a -> {
			a.setIp(utilIP.getClientIp());
			a.setFecSistema(ahora);
			a.setEstado(Constantes.ESTADO_PUBLICADO);
		});
		this.audienciaRepository.saveAll(audiencias);
	}

	@Override
	public List<ResponseAudienciaVO> getAudienciasPublicadas(List<String> instancias, LocalDateTime fecha) {
		LocalDateTime inicio = fecha;
		LocalDateTime fin = fecha.plusDays(1).minusSeconds(1);
		List<Audiencia> audiencias = this.audienciaRepository.findByIdInstanciaInAndFecAudienciaBetween(instancias, inicio, fin);
		List<Instancia> instanciasBD = this.instanciaRepository.findBySedeIdAndEspecialidad(audiencias.get(0).getIdSede(), audiencias.get(0).getEspecialidad());
		Map<String, String> mapInstancias = instanciasBD.stream().collect(Collectors.toMap(Instancia::getId, Instancia::getDenominacion));
		
		List<ResponseAudienciaVO> responseAudienciaVO = this.audienciaMapperService.convertir_a_VO(audiencias);
		responseAudienciaVO.stream().forEach(r -> {
			r.setInstancia(mapInstancias.get(r.getIdInstancia()));
			r.setIdEstado(r.getEstado());
			r.setEstado(r.getEstado().equals(Constantes.ESTADO_PUBLICADO) ? Constantes.TEXTO_PUBLICADO : Constantes.TEXTO_ANULADO);
		});
		return responseAudienciaVO;
	}

	@Override
	public ResponseAudienciaVO getAudiencia(Integer id) {
		Audiencia audiencia = this.audienciaRepository.findById(id).get();
		return this.audienciaMapperService.convertir_a_VO(audiencia);
	}

	@Override
	public void modificarAudiencia(RequestAudienciaVO requestAudienciaVO) {
		Audiencia audiencia = this.audienciaRepository.findById(requestAudienciaVO.getId()).get();
		requestAudienciaVO.setEnlace(requestAudienciaVO.getEnlace().toLowerCase().trim());
		if(requestAudienciaVO.getEnlace().length() != 0) {
			String ver = requestAudienciaVO.getEnlace().substring(0, 8);
			if(!ver.equals(Constantes.PROTOCOLO_WEB)) {
				requestAudienciaVO.setEnlace(Constantes.PROTOCOLO_WEB.concat(requestAudienciaVO.getEnlace()));
			}
		}
		audiencia.setEnlace(requestAudienciaVO.getEnlace());
		audiencia.setEstado(requestAudienciaVO.getEstado());
		this.audienciaRepository.save(audiencia);
	}

}
