package com.cdm.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.security.Principal;
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

import com.cdm.domain.UsuarioInterno;
import com.cdm.domain.vo.RequestAudienciaVO;
import com.cdm.domain.vo.ResponseAudienciaVO;
import com.cdm.domain.vo.ResponseEnlaceAudienciaVO;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.mapper.InstanciaMapperService;
import com.cdm.repository.InstanciaRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service.external.ServicioExternalService;
import com.cdm.service.external.vo.ResponseAudienciaAgendaExternalVO;
import com.cdm.service1.AudienciaService;
import com.cdm.service1.InstanciaService;
import com.cdm.service1.SedeService;

@Controller
public class AudienciaController {
	
	private SedeService sedeService;
	
	private InstanciaService instanciaService;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	private ServicioExternalService servicioExternalService;
	
	private AudienciaService audienciaService;
	
	public AudienciaController(SedeService sedeService, UsuarioInternoRepository usuarioInternoRepository,
		ServicioExternalService servicioExternalService, AudienciaService audienciaService, InstanciaService instanciaService) {
		this.sedeService = sedeService;
		this.usuarioInternoRepository= usuarioInternoRepository;
		this.servicioExternalService = servicioExternalService;
		this.audienciaService = audienciaService;
		this.instanciaService = instanciaService;
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
	
	@GetMapping("/getSedeAudiencia") 
	public @ResponseBody ResponseEntity<List<ResponseSedeVO>> getSedeAudiencia(String sede, String especialidad) {
		List<ResponseSedeVO> responseSedeVO = this.sedeService.getSedeAudiencia(); 
		return new ResponseEntity<>(responseSedeVO, HttpStatus.OK);
	}
	
	@GetMapping("/getInstanciaAudiencia") 
	public @ResponseBody ResponseEntity<List<ResponseInstanciaVO>> getInstanciaAudiencia(String sede, String especialidad) {
		List<ResponseInstanciaVO> responseInstanciaVOS = this.instanciaService.getInstanciasAudiencia(sede, especialidad);
		return new ResponseEntity<>(responseInstanciaVOS, HttpStatus.OK);
	}
	
	@GetMapping("/getAgendaSij") 
	public @ResponseBody ResponseEntity<List<ResponseAudienciaAgendaExternalVO>> getAgendaSij(String sede, 
			@RequestParam(value="instancias") List<String> instancias, String fecha) {
		List<ResponseAudienciaAgendaExternalVO> responseAudienciaAgendaExternalVO = this.servicioExternalService.getAgendaSij(sede, instancias, fecha); 
		return new ResponseEntity<>(responseAudienciaAgendaExternalVO, HttpStatus.OK);
	}
	
	@GetMapping("/getAudienciasPublicadas") 
	public @ResponseBody ResponseEntity<List<ResponseAudienciaVO>> getAudienciasPublicadas(@RequestParam(value="instancias") List<String> instancias, String fecha, 
			String especialidad) {
		List<ResponseAudienciaVO> responseAudienciaVO = this.audienciaService.getAudienciasPublicadas(instancias, fecha, especialidad); 
		return new ResponseEntity<>(responseAudienciaVO, HttpStatus.OK); 
	}
	
	@GetMapping("/getAudiencia") 
	public @ResponseBody ResponseEntity<ResponseAudienciaVO> getAudiencia(String sede, @RequestParam(value="id") Integer id) {
		ResponseAudienciaVO responseAudienciaVO = this.audienciaService.getAudiencia(id);
		return new ResponseEntity<>(responseAudienciaVO, HttpStatus.OK); 
	}
	
	@GetMapping("/getEnlaceAudiencia") 
	public @ResponseBody ResponseEntity<ResponseEnlaceAudienciaVO> getEnlaceAudiencia(@RequestParam(value="id") Integer id) {
		ResponseEnlaceAudienciaVO responseEnlaceAudienciaVO = this.audienciaService.getEnlaceAudiencia(id); 
		return new ResponseEntity<>(responseEnlaceAudienciaVO, HttpStatus.OK); 
	}

	@PostMapping(value = "/publicarAudiencias", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<ResponseEntity<HttpStatus>> publicarAudiencias(@Valid @RequestBody List<RequestAudienciaVO> requestAudienciaVO, Principal principal) {
	    this.audienciaService.publicarAudiencias(requestAudienciaVO);
		return new ResponseEntity<>(NO_CONTENT);
	}
	
	@PutMapping(value = "/modificarAudiencia", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<ResponseEntity<HttpStatus>> modificarAudiencia(@Valid @RequestBody RequestAudienciaVO requestAudienciaVO, Principal principal) {
	    this.audienciaService.modificarAudiencia(requestAudienciaVO);
		return new ResponseEntity<>(NO_CONTENT);
	}
}
