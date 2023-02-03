package com.cdm.service1.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain.CdgTurno;
import com.cdm.domain.SedeUsuario;
import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.RequestTurnoVO;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.domain.vo.ResponseCdgTurnoVO;
import com.cdm.mapper.CdgDocumentoMapperService;
import com.cdm.repository.CdgDocumentoRepository;
import com.cdm.repository.CdgTurnoRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service1.CdgDocumentoService;
import com.cdm.utils.Constantes;
import com.cdm.utils.UtilIP;

@Service
public class CdgDocumentoServiceImpl implements CdgDocumentoService{
	
	private static final String estado_cdg_pendiente = "P";
	
	private static final String documento_elevado = "S";
	
	private static final String documento_no_elevado = "N";
	
	private CdgDocumentoMapperService cdgDocumentoMapperService;
	
	private CdgDocumentoRepository cdgDocumentoRepository;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	private CdgTurnoRepository cdgTurnoRepository;
	
	public CdgDocumentoServiceImpl(CdgDocumentoMapperService cdgDocumentoMapperService,
		CdgDocumentoRepository cdgDocumentoRepository, UsuarioInternoRepository usuarioInternoRepository, CdgTurnoRepository cdgTurnoRepository) {
		this.cdgDocumentoMapperService = cdgDocumentoMapperService;
		this.cdgDocumentoRepository = cdgDocumentoRepository;
		this.usuarioInternoRepository = usuarioInternoRepository;
		this.cdgTurnoRepository = cdgTurnoRepository;
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
		
		if(usuarioInterno.getPerfil() == Constantes.USUARIO_PERFIL_JEFE_CDG) {
			if(!fecInicio.isEmpty() && !fecFin.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findByFecSistemaBetween(fechaInicio, fechaFin);
			else if(!parametro.isEmpty()) 
				cdgDocumentos = this.cdgDocumentoRepository.findByExpedienteContainingOrUsuarioExternoNombresContaining(parametro, parametro);
			else if(!sede.isEmpty())
				cdgDocumentos = this.cdgDocumentoRepository.findBySedeIdAndEstado(sede, "P");
			else
				cdgDocumentos = this.cdgDocumentoRepository.findBySedeIdAndEstado("0401", "P");
		}
		else if(usuarioInterno.getPerfil() == Constantes.USUARIO_PERFIL_ASISTENTE_CDM_CDG){
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

	@Transactional
	@Override
	public void guardarTurnoCdg(RequestTurnoVO requestTurnoVO) {
		
		String usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(requestTurnoVO.getTipo().equals("M")) {
			if(requestTurnoVO.getFechas() != null) {
				String[] cadenaAnioMes = requestTurnoVO.getFechas().split("-");
				Integer anio = Integer.parseInt(cadenaAnioMes[0]);
				Integer mes = Integer.parseInt(cadenaAnioMes[1]);
			
				LocalDateTime fechaActual = this.cdgDocumentoRepository.getFechaHoraActual().getFechaHora();
				
				YearMonth anioMesProgramar = YearMonth.of(anio, mes);
				YearMonth anioMesActual = YearMonth.of(fechaActual.getYear(), fechaActual.getMonthValue());
				
				Integer primerDia = (anioMesProgramar.isAfter(anioMesActual) ? 1 : fechaActual.getDayOfMonth());
				Integer ultimoDia = anioMesProgramar.lengthOfMonth();
				
				List<LocalDateTime> fechas = new ArrayList<>();
				
				for(int i = primerDia; i <= ultimoDia; i++) {
					fechas.add(LocalDateTime.of(anio, mes, i, 0, 0));
				}
				
				this.validaGuardaTurno(requestTurnoVO.getAsistenteCdg(), requestTurnoVO.getSede(), usuarioActual, fechas);

			}
		}
		else if(requestTurnoVO.getTipo().equals("D")) {
			List<String> separaFechas = new ArrayList<String>(Arrays.asList(requestTurnoVO.getFechas().split(",")));
			List<LocalDateTime> fechas = new ArrayList<>();
			separaFechas.stream().forEach(f -> {
				fechas.add(LocalDate.parse(f, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay());
			});
			this.validaGuardaTurno(requestTurnoVO.getAsistenteCdg(), requestTurnoVO.getSede(), usuarioActual, fechas);
		}
	}
	
	void validaGuardaTurno(String asistenteCdg, String sede, String usuarioWeb, List<LocalDateTime> fechas) {
		
		List<CdgTurno> cdgTurnos = new ArrayList<>();
		fechas.stream().forEach(f -> {
			CdgTurno cdgTurno = this.cdgTurnoRepository.findByAsistenteCdgAndSedeAndFecTurnoAndFecBajaIsNull(asistenteCdg, 
					sede, f);
			if(Objects.isNull(cdgTurno)) {
				cdgTurno = new CdgTurno();
				cdgTurno.setFecTurno(f);
				cdgTurno.setAsistenteCdg(asistenteCdg);
				cdgTurno.setSede(sede);
				cdgTurno.setJefeCdg(usuarioWeb);
				cdgTurno.setIp(UtilIP.getClientIp());
				cdgTurnos.add(cdgTurno);
			}
		});
		
		this.cdgTurnoRepository.saveAll(cdgTurnos);
	}
	
	@Transactional
	@Override
	public void anularTurnoCdg(Integer idTurno) {
		CdgTurno cdgTurno = this.cdgTurnoRepository.findById(idTurno).get();
		if(Objects.isNull(cdgTurno)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo recuperar información del turno, actualice el calendario");
		}
		else {
			if(cdgTurno.getFecBaja() != null) {
				throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "El turno seleccionado ya fue anulado, actualice el calendario");
			}
			else {
				cdgTurno.setFecBaja(this.cdgDocumentoRepository.getFechaHoraActual().getFechaHora());
				cdgTurno.setJefeCdgBaja(SecurityContextHolder.getContext().getAuthentication().getName());
				cdgTurno.setIpBaja(UtilIP.getClientIp());
				this.cdgTurnoRepository.save(cdgTurno);
			}
		}
	}

	@Override
	public List<ResponseCdgTurnoVO> getTurnosCdg(String sede, LocalDateTime inicio, LocalDateTime fin) {
		List<CdgTurno> cdgTurnos = this.cdgTurnoRepository.findBySedeAndFecTurnoBetweenAndFecBajaIsNull(sede, inicio, fin);
		List<String> idUsuarios = cdgTurnos.stream().map(CdgTurno::getAsistenteCdg).distinct().collect(Collectors.toList());
		List<UsuarioInterno> usuarioInternos = this.usuarioInternoRepository.findByUsuarioIn(idUsuarios);
		Map<String, String> mapUsuarios = usuarioInternos.stream().collect(Collectors.toMap(u -> u.getUsuario(), u -> u.getNombres().concat(" ").concat(u.getApePaterno()).concat(" ").concat(u.getApeMaterno())));
		List<ResponseCdgTurnoVO> responseCdgTurnoVOS = new ArrayList<>();
		cdgTurnos.stream().forEach(t -> {
			ResponseCdgTurnoVO responseCdgTurnoVO = new ResponseCdgTurnoVO();
			responseCdgTurnoVO.setId(t.getId());
			responseCdgTurnoVO.setTitle(mapUsuarios.get(t.getAsistenteCdg()));
			responseCdgTurnoVO.setStart(t.getFecTurno().toString());
			responseCdgTurnoVO.setEnd(t.getFecTurno().plusDays(1).toString());
			responseCdgTurnoVO.setColor("#0086E0");
			responseCdgTurnoVOS.add(responseCdgTurnoVO);
		});
		return responseCdgTurnoVOS;
	}

}
