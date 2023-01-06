package com.cdm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.service2.AuxilioJudicialService;
import com.cdm.service1.EspecialidadService;
import com.cdm.service2.OrganoJurisdiccionalService;

@Controller
public class AuxilioJudicialController {
	
	@Autowired
	private AuxilioJudicialService auxilioJudicialService;
	
	@Autowired
	private OrganoJurisdiccionalService organoJurisdiccionalService;
	
	@Autowired
	private EspecialidadService especialidadService;
	
	private static final String local = "E://temp_cdg/";
	
	@GetMapping("/getAuxiliosJudiciales")
	public String getAuxiliosJudiciales(Model model, Principal principal, @RequestParam(required = false) String inicio,
			@RequestParam(required = false) String fin) {
		try {
			List<ResponseAuxilioJudicialVO> responseAuxilioJudicialVOS = this.auxilioJudicialService.getAuxiliosJudiciales(
					principal.getName(), inicio, fin);	
			model.addAttribute("tipo", "SOLICITUDES AUX JUDICIAL");
			model.addAttribute("formulario", "AUXILIO JUDICIAL");
			model.addAttribute("registros", responseAuxilioJudicialVOS);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/auxiliojudicial/listaAuxilioJudicial";  
	}
	
	@GetMapping("/getAuxilioJudicial")
	public String getAuxilioJudicial(Model model, Principal principal, @RequestParam(value="id") Integer id) {
		model.addAttribute("datos", auxilioJudicialService.getAuxilioJudicial(principal.getName(),id));
		model.addAttribute("organos", organoJurisdiccionalService.getOrganosJurisdiccionales());
		model.addAttribute("especialidades", especialidadService.getEspecialidades());
		model.addAttribute("anio", auxilioJudicialService.getFechaActual().getFechaHora().getYear());
		model.addAttribute("sesion", principal.getName());
		return "vistas/auxiliojudicial/detalleAuxilioJudicial";  
	}
	
	@PutMapping(value = "/liberarAuxJud", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> liberarAuxJud(@Valid @RequestBody RequestAuxilioJudicialVO requestAuxilioJudicialVO, Principal principal) {
		requestAuxilioJudicialVO.setUsuario(principal.getName());
		auxilioJudicialService.liberarAuxilioJudicial(requestAuxilioJudicialVO);
	    return new ResponseEntity<>(NO_CONTENT);
	}
	
	@PutMapping(value = "/guardarRespuestaAuxJud", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> guardarRespuestaAuxJud(@Valid @RequestBody RequestAuxilioJudicialVO requestAuxilioJudicialVO, Principal principal) {
		requestAuxilioJudicialVO.setUsuario(principal.getName());
		auxilioJudicialService.updateAuxilioJudicial(requestAuxilioJudicialVO);
	    return new ResponseEntity<>(NO_CONTENT);
	}
	
	@GetMapping(value = "/descargarArchivoAuxilioJudicial", produces = {"application/json"})
	public @ResponseBody ResponseArchivoVO descargarArchivo(@RequestParam(value="tipo") Integer tipo, @RequestParam(value="id") Integer id) {
		return auxilioJudicialService.descargarArchivoAuxJud(tipo, id);
	}
	
	@RequestMapping(value="/verArchivoAuxilioJudicial/{name}", method=RequestMethod.GET)
	public ResponseEntity<Object> verRespuestaPdfCJ(@PathVariable("name") String nombre) throws FileNotFoundException, MessagingException {
		ResponseEntity<Object> responseEntity = null;
		File file = new File(local + nombre);    
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
        return responseEntity;
	}
}
