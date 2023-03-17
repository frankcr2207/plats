package com.cdm.service2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cdm.domain.SedeUsuario;
import com.cdm.domain2.SunarpSolicitud;
import com.cdm.domain2.SunarpSolicitudVer;
import com.cdm.domain2.vo.ResponseSunarpSolicitudVO;
import com.cdm.mapper.SunarpSolicitudMapperService;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository2.SunarpSolicitudArchivoRepository;
import com.cdm.repository2.SunarpSolicitudRepository;
import com.cdm.repository2.SunarpSolicitudVerRepository;
import com.cdm.service1.InstanciaService;
import com.cdm.service1.SedeService;
import com.cdm.service2.SunarpSolicitudService;
import com.cdm.utils.Constantes;

@Service
public class SunarpSolicitudServiceImpl implements SunarpSolicitudService {
	
	private SunarpSolicitudRepository sunarpSolicitudRepository;
	
	private SunarpSolicitudArchivoRepository sunarpSolicitudArchivoRepository;
	
	private SunarpSolicitudVerRepository sunarpSolicitudVerRepository;
	
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	private SedeService sedeService;
	
	private InstanciaService instanciaService;
	
	private SunarpSolicitudMapperService sunarpSolicitudMapperService;
	
	public SunarpSolicitudServiceImpl(SunarpSolicitudRepository sunarpSolicitudRepository, SedeService sedeService,
		SunarpSolicitudVerRepository sunarpSolicitudVerRepository, SunarpSolicitudMapperService sunarpSolicitudMapperService,
		SunarpSolicitudArchivoRepository sunarpSolicitudArchivoRepository, InstanciaService instanciaService) {
		this.sunarpSolicitudRepository = sunarpSolicitudRepository;
		this.sunarpSolicitudVerRepository = sunarpSolicitudVerRepository;
		this.sunarpSolicitudMapperService = sunarpSolicitudMapperService;
		this.sunarpSolicitudArchivoRepository = sunarpSolicitudArchivoRepository;
		this.sedeService = sedeService;
		this.instanciaService = instanciaService;
	}

	@Override
	public List<ResponseSunarpSolicitudVO> getSunarpSolicitudes(String sede) {
		List<ResponseSunarpSolicitudVO> responseSunarpSolicitudVOS = new ArrayList<>(); 
		List<String> estados = Arrays.asList(Constantes.ESTADO_SUNARP_PM, Constantes.ESTADO_SUNARP_T);
		List<SunarpSolicitud> sunarpSolicitudes = new ArrayList<>(); 
		List<String> sedes = new ArrayList<>();
		if(hasRole(Constantes.ROLE_ASISTENTE_CDM_CDG)) {
			List<SedeUsuario> sedeUsuario = this.sedeUsuarioRepository.findByUsuario(this.getUsuario());
			sedes = sedeUsuario.stream().map(SedeUsuario::getSede).collect(Collectors.toList());
			sunarpSolicitudes = this.sunarpSolicitudRepository.findByEstadoInAndIdSedeIn(estados, sedes);
		}
		else if(hasRole(Constantes.ROLE_JEFE_CDG)){
			if(sede != null) {
				sedes.add(sede);
				sunarpSolicitudes = this.sunarpSolicitudRepository.findByEstadoInAndIdSedeIn(estados, sedes);
			}
			else {
				sunarpSolicitudes = this.sunarpSolicitudRepository.findByEstadoIn(estados);
			}
		}
		
		List<Integer> ids = sunarpSolicitudes.stream().map(SunarpSolicitud::getId).collect(Collectors.toList());
		List<SunarpSolicitudVer> sunarpSolicitudVer = this.sunarpSolicitudVerRepository.findByIdSolicitudInAndUltimo(ids, "S");
		
		Map<Integer, List<SunarpSolicitudVer>> mapVersiones = sunarpSolicitudVer.stream().collect(Collectors.groupingBy(s -> s.getIdSolicitud()));
		sunarpSolicitudes.stream().forEach(s -> {
			if(mapVersiones.containsKey(s.getId())) {
				ResponseSunarpSolicitudVO response = this.sunarpSolicitudMapperService.convertir_a_VO(s);
				response.setFecIngreso(mapVersiones.get(s.getId()).get(0).getFecSistema());
				responseSunarpSolicitudVOS.add(response);
			}
		});
		return responseSunarpSolicitudVOS;
	}

	@Override
	public ResponseSunarpSolicitudVO getSunarpSolicitud(Integer id) {
		List<String> estados = Arrays.asList(Constantes.ESTADO_SUNARP_PM, Constantes.ESTADO_SUNARP_PS);
		SunarpSolicitud sunarpSolicitud = this.sunarpSolicitudRepository.findById(id).get();
		if(sunarpSolicitud.getEstado().equals(Constantes.ESTADO_SUNARP_PM)) {
			sunarpSolicitud.setEstado(Constantes.ESTADO_SUNARP_T);
			sunarpSolicitud.setUsuario(this.getUsuario());
			this.sunarpSolicitudRepository.save(sunarpSolicitud);
		}
		List<SunarpSolicitudVer> sunarpSolicitudVer = this.sunarpSolicitudVerRepository.findByIdSolicitudAndEstadoIn(id, estados);
		ResponseSunarpSolicitudVO response = this.sunarpSolicitudMapperService.convertir_a_VO(sunarpSolicitud);
		response.setFecIngreso(sunarpSolicitudVer.stream().filter(s -> s.getEstado().
			equals(Constantes.ESTADO_SUNARP_PM)).findFirst().orElse(null).getFecSistema());
		response.setArchivos(this.sunarpSolicitudArchivoRepository.findByIdSolicitud(sunarpSolicitud.getId()));
		response.setSede(this.sedeService.getSede(response.getIdSede()).getDenominacion());
		response.setInstancia(this.instanciaService.getInstancia(response.getIdInstancia()).getDenominacion());
		if(sunarpSolicitud.getEstado().equals(Constantes.ESTADO_SUNARP_PS))
			response.setFecAtencion(sunarpSolicitudVer.stream().filter(s -> s.getEstado().
					equals(Constantes.ESTADO_SUNARP_PS)).findFirst().orElse(null).getFecSistema());
		return response;
	}

	private boolean hasRole(String role) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role));
	}
	
	private String getUsuario() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
