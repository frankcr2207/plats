package com.cdm.controller;

import static org.springframework.http.HttpStatus.OK;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain.vo.RequestCasillaElectronicaVO;
import com.cdm.domain.vo.ResponseCasillaElectronicaVO;
import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.service1.CasillaElectronicaService;
import com.cdm.utils.Constantes;

@Controller
public class CasillaElectronicaController {
	
	@Autowired
	private CasillaElectronicaService casillaElectronicaService;
	
	@GetMapping("/getCasillasElectronicas")
	public String getCasillasJudiciales(Model model, Principal principal){
		model.addAttribute("tipo", "SOLIDITUDES");
		model.addAttribute("formulario", "AUXILIO JUDICIAL");
		model.addAttribute("registros", casillaElectronicaService.getSolicitudes());
		return "vistas/casillaelectronica/listaCasillaJudicial";
	}
	
	@GetMapping(value = "/getCasillaElectronica", produces = {"application/json"})
	public String getCasillaJudicial(Model model, Principal principal, @RequestParam Integer id){
		model.addAttribute("datos", casillaElectronicaService.getSolicitud(id));
		return "vistas/casillaelectronica/detalleCasillaElectronica";
	}
	
	@GetMapping(value = "/findCasillasElectronicas", produces = {"application/json"})
	public List<ResponseCasillaElectronicaVO> findCasillaJudicial(@RequestParam String parametro){
		return casillaElectronicaService.getSolicitudesPorUsuario(parametro);
	}
	
	@GetMapping(value = "/descargarArchivoCasillaElectronica")
	public @ResponseBody String descargarArchivoCasillaElectronica(@RequestParam(value="tipo") Integer tipo, @RequestParam(value="id") Integer id) {
		return casillaElectronicaService.descargarArchivoCasillaElectronica(tipo, id);
	}
	
	@RequestMapping(value="/verArchivoCasillaElectronica/{name}", method=RequestMethod.GET)
	public ResponseEntity<Object> verRespuestaPdfCJ(@PathVariable("name") String nombre) throws FileNotFoundException, MessagingException {
		ResponseEntity<Object> responseEntity = null;
		File file = new File(Constantes.RUTA_SERVIDOR_LOCAL_CDG + nombre);    
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
        return responseEntity;
	}
	
	@PutMapping(value = "/guardarRespuestaCasillaElec", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<HttpStatus> guardarRespuestaCasdillaElec(@Valid @ModelAttribute RequestCasillaElectronicaVO requestCasillaElectronicaVO, Principal principal) {
		requestCasillaElectronicaVO.setUsuario(principal.getName());
		casillaElectronicaService.updateCasillaElectronica(requestCasillaElectronicaVO);
	    return new ResponseEntity<>(OK);
	}
}
