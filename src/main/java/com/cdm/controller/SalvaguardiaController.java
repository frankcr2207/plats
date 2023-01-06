package com.cdm.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.RequestSalvaguardiaVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseSalvaguardiaVO;
import com.cdm.service1.InstanciaService;
import com.cdm.service1.SedeService;
import com.cdm.service2.SalvaguardiaService;

@Controller
public class SalvaguardiaController {
	
	@Autowired
	private SalvaguardiaService salvaguardiaService;
	
	@Autowired
	private SedeService sedeService;
	
	@Autowired
	private InstanciaService instanciaService;
	
	private static final String local = "E://temp_cdg/";
	
	@GetMapping("/getSalvaguardias")
	public String getSalvaguardias(Model model, Principal principal, @RequestParam(required = false) String inicio,
			@RequestParam(required = false) String fin) {
		try {
			List<ResponseSalvaguardiaVO> responseSalvaguardiaVOS = this.salvaguardiaService.getSalvaguardias(
					principal.getName(), inicio, fin);
			model.addAttribute("tipo", "SOLICITUDES SALVAGUARDIA");
			model.addAttribute("registros", responseSalvaguardiaVOS);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/salvaguardia/listaSalvaguardia";  
	}
	
	@GetMapping("/getSalvaguardia")
	public String getSalvaguardia(Model model, Principal principal, @RequestParam(value="id") Integer id) {
		ResponseSalvaguardiaVO responseSalvaguardiaVO = salvaguardiaService.getSalvaguardia(principal.getName(), id);
		model.addAttribute("datos", responseSalvaguardiaVO);
		model.addAttribute("anio", salvaguardiaService.getFechaHoraActual().getFechaHora().getYear());
		model.addAttribute("instancias", instanciaService.getInstanciasSalvaguardia(responseSalvaguardiaVO.getSede()));
		model.addAttribute("organos", Arrays.asList("JR","JM"));
		model.addAttribute("especialidades", Arrays.asList("FC"));
		model.addAttribute("sesion", principal.getName());
		return "vistas/salvaguardia/detalleSalvaguardia";  
	}
	
	@GetMapping(value = "/descargarArchivoSalvaguardia", produces = {"application/json"})
	public @ResponseBody ResponseArchivoVO descargarArchivo(@RequestParam(value="tipo") Integer tipo, @RequestParam(value="id") Integer id) {
		return salvaguardiaService.descargarArchivoSalvaguardia(tipo, id);
	}
	
	@RequestMapping(value="/verArchivoSalvaguardia/{name}", method=RequestMethod.GET)
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
	
	@PutMapping(value = "/liberarSalvaguardia", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> liberarSalvaguardia(@Valid @RequestBody RequestSalvaguardiaVO requestSalvaguardiaVO, Principal principal) {
		requestSalvaguardiaVO.setUsuario(principal.getName());
		salvaguardiaService.liberarSalvaguardia(requestSalvaguardiaVO);
	    return new ResponseEntity<>(NO_CONTENT);
	}
	
	@PutMapping(value = "/guardarRespuestaSalvaguardia", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> guardarRespuestaSalvaguardia(@Valid @RequestBody RequestSalvaguardiaVO requestSalvaguardiaVO, Principal principal) throws Exception {
		requestSalvaguardiaVO.setUsuario(principal.getName());
		salvaguardiaService.updateSalvaguardia(requestSalvaguardiaVO);
	    return new ResponseEntity<>(NO_CONTENT);
	}
	
	@GetMapping(value = "/descargarGeneralSalvaguardia", produces = {"application/json"})
	public @ResponseBody ResponseArchivoVO descargarGeneral(@RequestParam(value="id") Integer id) throws Exception {
		return salvaguardiaService.descargarArchivoGeneralSalvaguardia(null, null, id);
	}
}
