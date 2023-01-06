package com.cdm.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.cdm.domain2.vo.ResponseInpeSolicitudVO;
import com.cdm.domain2.vo.RequestInpeSolicitudVO;
import com.cdm.domain2.vo.RequestSalvaguardiaVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.service2.InpeSolicitudService;

@Controller
public class InpeSolicitudController {
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private InpeSolicitudService inpeSolicitudService;
	
	@GetMapping("/getInpeSolicitudes")
	public String getInpeSolicitudes(Model model,  @RequestParam(required = false) String inicio, 
			 @RequestParam(required = false) String fin, Principal principal) {
		try {
			List<ResponseInpeSolicitudVO> responseInpeSolicitudVOS = this.inpeSolicitudService.getInpeSolicitudes(
					inicio, fin, principal.getName());
			model.addAttribute("tipo", "SOLICITUDES INPE");
			model.addAttribute("registros", responseInpeSolicitudVOS);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/inpesolicitud/listaInpeSolicitud";  
	}
	
	@GetMapping("/getInpeSolicitud")
	public String getInpeSolicitud(Model model, Principal principal, @RequestParam(value="id") Integer id) {
		try {
			ResponseInpeSolicitudVO responseInpeSolicitudVO = this.inpeSolicitudService.getInpeSolicitud(principal.getName(), id);
			model.addAttribute("anio", inpeSolicitudService.getFechaHoraActual().getFechaHora().getYear());
			model.addAttribute("datos", responseInpeSolicitudVO);
			model.addAttribute("sesion", principal.getName());
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/inpesolicitud/detalleInpeSolicitud";  
	}
	
	@PutMapping(value = "/liberarInpeSolicitud", consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<HttpStatus> liberarInpeSolicitud(@Valid @RequestBody RequestInpeSolicitudVO requestInpeSolicitudVO, Principal principal) {
		requestInpeSolicitudVO.setUsuario(principal.getName());
		inpeSolicitudService.liberarInpeSolicitud(requestInpeSolicitudVO);
	    return new ResponseEntity<>(NO_CONTENT);
	}
	
	@PutMapping(value = "/guardarRespuestaInpeSolicitud", consumes = {"application/json"})
	public @ResponseBody ResponseEntity<String> guardarRespuestaInpeSolicitud(@Valid @RequestBody RequestInpeSolicitudVO requestInpeSolicitudVO, Principal principal) throws Exception {
		requestInpeSolicitudVO.setUsuario(principal.getName());
		String archivoFinal = inpeSolicitudService.updateInpeSolicitud(requestInpeSolicitudVO);
	    return new ResponseEntity<>(archivoFinal, HttpStatus.OK);
	}
	
	@GetMapping(value = "/descargarArchivoInpeSolicitud")
	public @ResponseBody String descargarArchivoInpeSolicitud(@RequestParam(value="tipo") Integer tipo,
			@RequestParam(value="id") Integer id) {
		return inpeSolicitudService.descargarArchivoInpeSolicitud(tipo, id);
	}
	
	@RequestMapping(value="/verArchivoInpeSolicitud/{name}", method=RequestMethod.GET)
	public ResponseEntity<Object> verArchivoInpeSolicitud(@PathVariable("name") String nombre) throws FileNotFoundException, MessagingException {
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
