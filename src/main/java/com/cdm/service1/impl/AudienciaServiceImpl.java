package com.cdm.service1.impl;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.cdm.domain.Audiencia;
import com.cdm.domain.AudienciaVer;
import com.cdm.domain.Instancia;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;
import com.cdm.domain.vo.ResponseEnlaceAudienciaVO;
import com.cdm.mapper.AudienciaMapperService;
import com.cdm.repository.AudienciaRepository;
import com.cdm.repository.AudienciaVerRepository;
import com.cdm.repository.InstanciaRepository;
import com.cdm.service1.AudienciaService;
import com.cdm.utils.Constantes;
import com.cdm.utils.UtilIP;

@Service
public class AudienciaServiceImpl implements AudienciaService {
	
	private InstanciaRepository instanciaRepository;
	
	private AudienciaRepository audienciaRepository;
	
	private AudienciaVerRepository audienciaVerRepository;
	
	private AudienciaMapperService audienciaMapperService;
	
	public AudienciaServiceImpl(AudienciaRepository audienciaRepository,
			AudienciaMapperService audienciaMapperService, InstanciaRepository instanciaRepository,
			AudienciaVerRepository audienciaVerRepository) {
		this.audienciaRepository = audienciaRepository;
		this.audienciaMapperService = audienciaMapperService;
		this.instanciaRepository = instanciaRepository;
		this.audienciaVerRepository = audienciaVerRepository;
	}
	
	@Transactional
	@Override
	public void publicarAudiencias(List<RequestAudienciaVO> requestAudienciaVO) {
		
		String ip = UtilIP.getClientIp();
		String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		LocalDateTime ahora = this.audienciaRepository.getFechaHoraActual().getFechaHora();
		List<Audiencia> audiencias = new ArrayList<>();
		List<AudienciaVer> audienciasVer = new ArrayList<>();
		
		requestAudienciaVO.stream().forEach(r -> {
			if(r.getEstado().equals(Constantes.ESTADO_AUDIENCIA_PUBLICADO) && (r.getEnlace() == null || r.getEnlace().isEmpty()))
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Existen registros con estado publicado sin enlace, verifique!!");
			
			Optional<Audiencia> audiencia = this.audienciaRepository.findByExpedienteAndIdInstanciaAndEspecialidadAndFecAudiencia(
					r.getExpediente(), r.getIdInstancia(), r.getEspecialidad(), r.getFecAudiencia());
			if(!audiencia.isPresent()) {
				audiencias.add(this.audienciaMapperService.convertir_a_entidad(r));
			}
		});
		audiencias.stream().forEach(a -> {
			a.setIp(ip);
			a.setUsuario(currentUserName);
			a.setFecSistema(ahora);
			a.setEnlace(getEnlace(a.getEnlace().toLowerCase().trim()));
			if(a.getEnlace().length() != 0) {
				String ver = a.getEnlace().substring(0, 8);
				if(!ver.equals(Constantes.PROTOCOLO_WEB)) {
					a.setEnlace(Constantes.PROTOCOLO_WEB.concat(a.getEnlace()));
				}
			}
		});
		List<Audiencia> audienciasBd = audienciaRepository.saveAll(audiencias);
			
		audienciasBd.stream().forEach(a -> {
			AudienciaVer audienciaVer = new AudienciaVer();
			audienciaVer.setIdAudiencia(a.getId());
			audienciaVer.setVersion(1);
			audienciaVer.setEstado(a.getEstado());
			audienciaVer.setEnlace(a.getEnlace());
			audienciaVer.setIp(ip);
			audienciaVer.setUsuario(currentUserName);
			audienciaVer.setFecSistema(a.getFecSistema());
			audienciasVer.add(audienciaVer);
		});
		audienciaVerRepository.saveAll(audienciasVer);
	}
	
	@Override
	public List<ResponseAudienciaVO> getAudienciasPublicadas(List<String> instancias, String fecha, String especialidad) {

		List<Audiencia> audiencias = new ArrayList<>();
		LocalDateTime ahora = LocalDateTime.now().minusMinutes(Constantes.LIMITE_MINUTOS_LISTA_AUDIENCIA).withSecond(0).withNano(0);
		if(especialidad != null && !especialidad.isEmpty())
			audiencias = this.audienciaRepository.findByEspecialidadAndFecAudienciaGreaterThanEqualAndEstadoIn(especialidad, ahora, Arrays.asList(Constantes.ESTADO_AUDIENCIA_PUBLICADO, Constantes.ESTADO_AUDIENCIA_RESERVADO));
		else {
			LocalDateTime fechaFormateada = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
			LocalDateTime inicio = fechaFormateada;
			LocalDateTime fin = fechaFormateada.plusDays(1).minusSeconds(1);
			audiencias = this.audienciaRepository.findByIdInstanciaInAndFecAudienciaBetween(instancias, inicio, fin);
		}
		List<ResponseAudienciaVO> responseAudienciaVO = new ArrayList<>();
		if(!audiencias.isEmpty()) {
			List<Instancia> instanciasBD = this.instanciaRepository.findBySedeIdAndEspecialidad(audiencias.get(0).getIdSede(), audiencias.get(0).getEspecialidad());
			Map<String, String> mapInstancias = instanciasBD.stream().collect(Collectors.toMap(Instancia::getId, Instancia::getDenominacion));
			responseAudienciaVO = this.audienciaMapperService.convertir_a_VO(audiencias);
			responseAudienciaVO.stream().forEach(r -> {
				r.setInstancia(mapInstancias.get(r.getIdInstancia()));
				r.setIdEstado(r.getEstado());
				r.setHora(r.getFecAudiencia());
				r.setEnlace(especialidad == null ? r.getEnlace() : null);
				r.setEstado(r.getEstado().equals(Constantes.ESTADO_AUDIENCIA_PUBLICADO) ? Constantes.TEXTO_PUBLICADO : (r.getEstado().equals(Constantes.ESTADO_AUDIENCIA_RESERVADO) ? Constantes.TEXTO_RESERVADO: Constantes.TEXTO_ANULADO));
			});
		}
		
		return responseAudienciaVO;
	}

	@Override
	public ResponseAudienciaVO getAudiencia(Integer id) {
		Audiencia audiencia = this.audienciaRepository.findById(id).get();
		ResponseAudienciaVO responseAudienciaVO = this.audienciaMapperService.convertir_a_VO(audiencia);
		responseAudienciaVO.setHora(responseAudienciaVO.getFecAudiencia());
		return responseAudienciaVO;
	}
	
	@Transactional
	@Override
	public void modificarAudiencia(RequestAudienciaVO requestAudienciaVO) {
		Audiencia audiencia = this.audienciaRepository.findById(requestAudienciaVO.getId()).get();
		
		if(requestAudienciaVO.getEstado().equals(Constantes.ESTADO_AUDIENCIA_PUBLICADO) && (requestAudienciaVO.getEnlace() == null || requestAudienciaVO.getEnlace().isEmpty()))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El estado es PUBLICADO debe consignar el enlace, verifique!!");
		
		requestAudienciaVO.setEnlace(requestAudienciaVO.getEnlace().toLowerCase().trim());
		if(requestAudienciaVO.getEnlace().length() != 0) {
			String ver = requestAudienciaVO.getEnlace().substring(0, 8);
			if(!ver.equals(Constantes.PROTOCOLO_WEB)) {
				requestAudienciaVO.setEnlace(Constantes.PROTOCOLO_WEB.concat(requestAudienciaVO.getEnlace()));
			}
		}
		String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
		LocalDateTime ahora = this.audienciaRepository.getFechaHoraActual().getFechaHora();
		audiencia.setEnlace(requestAudienciaVO.getEnlace());
		audiencia.setEstado(requestAudienciaVO.getEstado());
		audiencia.setUsuarioModifica(currentUserName);
		audiencia.setFecModificacion(ahora);
		this.audienciaRepository.save(audiencia);
		
		UtilIP utilIP = new UtilIP();
		String ip = utilIP.getClientIp();
		
		List<AudienciaVer> audienciasVer = this.audienciaVerRepository.findByIdAudienciaOrderByVersionDesc(audiencia.getId());
		
		AudienciaVer audienciaVer = new AudienciaVer();
		audienciaVer.setIdAudiencia(audiencia.getId());
		audienciaVer.setVersion(audienciasVer.isEmpty() ? 2 :audienciasVer.get(0).getVersion() + 1);
		audienciaVer.setEstado(audiencia.getEstado());
		audienciaVer.setEnlace(audiencia.getEnlace());
		audienciaVer.setIp(ip);
		audienciaVer.setUsuario(currentUserName);
		audienciaVer.setFecSistema(ahora);
		
		this.audienciaVerRepository.save(audienciaVer);
	}

	@Override
	public ResponseEnlaceAudienciaVO getEnlaceAudiencia(Integer id) {
		Audiencia audiencia = this.audienciaRepository.findById(id).get();
		LocalDateTime ahora = this.audienciaRepository.getFechaHoraActual().getFechaHora();
		ResponseEnlaceAudienciaVO responseEnlaceAudienciaVO = new ResponseEnlaceAudienciaVO();
		if(audiencia.getEstado().equals(Constantes.ESTADO_AUDIENCIA_ANULADO) || audiencia.getEstado().equals(Constantes.ESTADO_AUDIENCIA_RESERVADO)) {
			responseEnlaceAudienciaVO.setStatus(Constantes.RESPUESTA_KO);
			responseEnlaceAudienciaVO.setMensaje("La audiencia ha cambiado de estado, no es posible ingresar");
		}
		else if(ahora.isBefore(audiencia.getFecAudiencia().minusMinutes(Constantes.LIMITE_MINUTOS_ANTES_AUDIENCIA))) {
			responseEnlaceAudienciaVO.setStatus(Constantes.RESPUESTA_KO);
			responseEnlaceAudienciaVO.setMensaje("La fecha/hora de audiencia aún no ha iniciado");
		}
		else if(ahora.isAfter(audiencia.getFecAudiencia().plusMinutes(Constantes.LIMITE_MINUTOS_DESPUES_AUDIENCIA))){
			responseEnlaceAudienciaVO.setStatus(Constantes.RESPUESTA_KO);
			responseEnlaceAudienciaVO.setMensaje("El límite de ingreso a la audiencia ha finalizado");
		}
		else {
			responseEnlaceAudienciaVO.setStatus(Constantes.RESPUESTA_OK);
			responseEnlaceAudienciaVO.setMensaje("Estimado visitante, recuerde que al acceder deberá <strong> deshabilitar micrófono y cámara web, " + 
			"caso contrario será retirado inmediatamente de la misma. </strong><br><br>Haga click en el siguiente botón:<br><br>" +
            "<button class='btn btn-light' onclick=\"window.open('" + audiencia.getEnlace()+ 
            "', '_blank');\"><img src='img/meet.png' align='middle' width='30'>&nbsp;<strong>INGRESAR</strong></button><br><br><span style='font-size: 11px'><strong>");
		}
		return responseEnlaceAudienciaVO;
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
