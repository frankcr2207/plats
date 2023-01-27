package com.cdm.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain.vo.RequestTurnoVO;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.domain.vo.ResponseCdgTurnoVO;
import com.cdm.domain.vo.ResponseUsuarioInternoVO;
import com.cdm.service1.CdgDocumentoService;
import com.cdm.service1.SedeService;
import com.cdm.service1.UsuarioInternoService;
import com.cdm.utils.Constantes;

@Controller
public class CdgController {
	
	private CdgDocumentoService cdgDocumentoService;
	
	private SedeService sedeService;
	
	private UsuarioInternoService usuarioInternoService;
	
	public CdgController(CdgDocumentoService cdgDocumentosService, SedeService sedeService, UsuarioInternoService usuarioInternoService) {
		this.cdgDocumentoService = cdgDocumentosService;
		this.sedeService = sedeService;
		this.usuarioInternoService = usuarioInternoService;
	}
	
	@GetMapping("/MESAPARTES")
	public String getInpeSolicitudes(Model model, Principal principal, @RequestParam(defaultValue = "") String sede, 
			 @RequestParam(defaultValue = "") String fecInicio, @RequestParam(defaultValue = "") String fecFin, 
			 @RequestParam(defaultValue = "") String parametro) {
		try {
			List<ResponseCdgDocumentoVO> responseCdgDocumentoVOS = this.cdgDocumentoService.getCdgDocumentos(
					principal.getName(), sede, fecInicio, fecFin, parametro);
			model.addAttribute("tipo", "MPE - DOCUMENTOS PENDIENTES");
			model.addAttribute("documentos", responseCdgDocumentoVOS);
			model.addAttribute("sedes", this.sedeService.getSedes());
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/cdg/listaCdgDocumento";  
	}
	
	@GetMapping("/URGENTES")
	public String urgentes(Model model, Principal principal) {
		try {
			model.addAttribute("documentos", this.cdgDocumentoService.getCdgDocumentosUrgentes());
			model.addAttribute("tipo", "MPE - URGENTES");
			model.addAttribute("formulario", "URGENTES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/cdg/listaCdgDocumento";
	}
	
	@GetMapping("/programacion") 
	public String programacionCdg(Model model, Principal principal) {
		ResponseUsuarioInternoVO responseUsuarioInternoVO = this.usuarioInternoService.getUsuarioInterno(principal.getName());
		if(responseUsuarioInternoVO.getPerfil() == Constantes.USUARIO_PERFIL_JEFE_CDG) {
			model.addAttribute("sedes", this.sedeService.getSedes());
		}
		else if(responseUsuarioInternoVO.getPerfil() == Constantes.USUARIO_PERFIL_ADMIN_MODULO) {
			model.addAttribute("sedes", this.sedeService.getSedesPorUsuario(principal.getName()));
		}
			
		return "vistas/cdg/programacion";
	}
	
	@GetMapping("/getUsuariosBySede") 
	public @ResponseBody ResponseEntity<List<ResponseUsuarioInternoVO>> getUsuariosBySede(String sede) {
		List<ResponseUsuarioInternoVO> responseUsuarioInternoVOS = this.usuarioInternoService.getUsuariosBySede(sede);
		return new ResponseEntity<>(responseUsuarioInternoVOS, HttpStatus.OK);
	}
	
	@PostMapping(value="/guardarTurnoCdg", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> guardarTurnoCdg(@RequestBody RequestTurnoVO requestTurnoVO) {
		this.cdgDocumentoService.guardarTurnoCdg(requestTurnoVO);
		return new ResponseEntity<>("Turnos guardados con éxito!!", HttpStatus.OK);
	}
	
	@DeleteMapping("/anularTurnoCdg/{idTurno}")
	public @ResponseBody ResponseEntity<String> anularTurnoCdg(@PathVariable Integer idTurno) {
		this.cdgDocumentoService.anularTurnoCdg(idTurno);
		return new ResponseEntity<>("Turno anulado con éxito!!", HttpStatus.OK);
	}
	
	@GetMapping("/getTurnosCdg")
	public @ResponseBody ResponseEntity<List<ResponseCdgTurnoVO>> getTurnosCdg(String sede, @RequestParam(value="start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
		@RequestParam(value="end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		List<ResponseCdgTurnoVO> turnos = this.cdgDocumentoService.getTurnosCdg(sede, inicio, fin);
		return new ResponseEntity<>(turnos, HttpStatus.OK);
	}
}
