package com.cdm.controller.rest;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.domain.vo.RequestGestionMultaVO;
import com.cdm.domain.vo.RequestNuevaMultaVO;
import com.cdm.domain.vo.ResponseConsultaDocumento;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseMultaVO;
import com.cdm.service1.InstanciaService;
import com.cdm.service1.MultaService;

@RestController
@CrossOrigin(origins="http://localhost:8080")
public class ControladorRestMulta {
	
	private MultaService multaService;
	
	private InstanciaService instanciaService;
	
	public ControladorRestMulta(MultaService multaService, InstanciaService instanciaService) {
		this.multaService = multaService;
		this.instanciaService = instanciaService;
	}	
	
	@GetMapping(value = "/getMultasSec", produces = {"application/json"})
	public ResponseEntity<List<ResponseMultaVO>> getMultasSec(Principal principal, @RequestParam(required = false) String parametro){
		List<ResponseMultaVO> responseMultaVOS = this.multaService.getMultasSec(principal.getName(), parametro);
		return new ResponseEntity<>(responseMultaVOS, HttpStatus.OK);
	}
	
	@GetMapping(value = "/consultarServicios", produces = {"application/json"})
	public ResponseEntity<ResponseConsultaDocumento> consultaServicios(@RequestParam String servicio, @RequestParam String documento){
		return new ResponseEntity<>(this.multaService.consultaServicios(servicio, documento), HttpStatus.OK);
	}
	
	@PostMapping(value = "/guardarMulta", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<ResponseMultaVO> guardaMulta(@RequestBody RequestNuevaMultaVO requestNuevaMultaVO, Principal principal){
		return new ResponseEntity<>(this.multaService.guardarMulta(requestNuevaMultaVO, principal.getName()), HttpStatus.OK);
	}

	@PutMapping(value = "/guardarGestionMulta", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<ResponseMultaVO> guardarGestion(@RequestBody RequestGestionMultaVO requestGestionMultaVO, Principal principal){
		return new ResponseEntity<>(this.multaService.guardarGestionMulta(requestGestionMultaVO, principal.getName()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/getInstanciasMulta", produces = {"application/json"})
	public ResponseEntity<List<ResponseInstanciaVO>> getInstanciasMulta(@RequestParam String sede){
		List<ResponseInstanciaVO> responseInstanciaVOS = this.instanciaService.getInstancias(sede);
		return new ResponseEntity<>(responseInstanciaVOS, HttpStatus.OK);
	}
}
