package com.cdm.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain.Instancia;
import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.mapper.InstanciaMapperService;
import com.cdm.mapper.SedeMapperService;
import com.cdm.repository.InstanciaRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service.external.ServicioExternalService;
import com.cdm.service.external.vo.ResponseAudienciaAgendaExternalVO;
import com.cdm.service1.AudienciaService;
import com.cdm.service1.SedeService;

@Controller
public class AudienciaController {
	
	private SedeService sedeService;
	
	private InstanciaRepository instanciaRepository;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	private InstanciaMapperService instanciaMapperService;
	
	private ServicioExternalService servicioExternalService;
	
	private AudienciaService audienciaService;
	
	public AudienciaController(SedeService sedeService, UsuarioInternoRepository usuarioInternoRepository,
		InstanciaRepository instanciaRepository, InstanciaMapperService instanciaMapperService,
		ServicioExternalService servicioExternalService, AudienciaService audienciaService) {
		this.sedeService = sedeService;
		this.usuarioInternoRepository= usuarioInternoRepository;
		this.instanciaRepository = instanciaRepository;
		this.instanciaMapperService = instanciaMapperService;
		this.servicioExternalService = servicioExternalService;
		this.audienciaService = audienciaService;
	}
	
	@GetMapping("/getPublicacionAudiencia") 
	public String getPublicacionAudiencias(Model model, Principal principal) {
		List<ResponseSedeVO> sedes = this.sedeService.getSedes();
		UsuarioInterno usuario = this.usuarioInternoRepository.findById(principal.getName()).get();
		model.addAttribute("tipo", "AUDIENCIAS VIRTUALES GOOGLE MEET");
		model.addAttribute("sedes", sedes);
		model.addAttribute("formulario", "AUDIENCIAS");
		model.addAttribute("especialidadUsuario", usuario.getEspecialidad());
		return "vistas/audiencia/audiencia";
	}
	
	@GetMapping("/getInstanciaAudiencia") 
	public @ResponseBody List<ResponseInstanciaVO> getInstanciaAudiencia(String sede, String especialidad) {
		List<Instancia> instancias = this.instanciaRepository.findBySedeIdAndEspecialidad(sede, especialidad);
		return this.instanciaMapperService.convertir_a_VO(instancias);
	}
	
	@GetMapping("/getAgendaSij") 
	public @ResponseBody List<ResponseAudienciaAgendaExternalVO> getAgendaSij(String sede, 
			@RequestParam(value="instancias") List<String> instancias, String fecha) {
		return this.servicioExternalService.getAgendaSij(sede, instancias, fecha);  
	}
	
	@GetMapping("/getAudienciasPublicadas") 
	public @ResponseBody List<ResponseAudienciaVO> getAudienciasPublicadas(String sede, @RequestParam(value="instancias") List<String> instancias, String fecha) {
		LocalDateTime fechaFormateada = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		return this.audienciaService.getAudienciasPublicadas(instancias, fechaFormateada);  
	}
	
	@GetMapping("/getAudiencia") 
	public @ResponseBody ResponseAudienciaVO getAudiencia(String sede, @RequestParam(value="id") Integer id) {
		return this.audienciaService.getAudiencia(id);  
	}

	@PostMapping(value = "/publicarAudiencias", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> publicarAudiencias(@Valid @RequestBody List<RequestAudienciaVO> requestAudienciaVO, Principal principal) {
	    this.audienciaService.publicarAudiencias(requestAudienciaVO);
		return new ResponseEntity<>(NO_CONTENT);
	}
	
	@PutMapping(value = "/modificarAudiencia", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> modificarAudiencia(@Valid @RequestBody RequestAudienciaVO requestAudienciaVO, Principal principal) {
	    this.audienciaService.modificarAudiencia(requestAudienciaVO);
		return new ResponseEntity<>(NO_CONTENT);
	}
}
