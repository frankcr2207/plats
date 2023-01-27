package com.cdm.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.cdm.domain.vo.RequestActaVO;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.service.external.ServicioExternalService;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;
import com.cdm.service1.ExcelService;
import com.cdm.service1.SedeService;
import com.cdm.utils.Constantes;
import com.cdm.utils.JxlsUtils;
import com.cdm.utils.PdfListaActas;

@Controller
public class ActaController {
	
	private ServicioExternalService servicioExternalService;
	
	private SedeService sedeService;
	
	private ExcelService excelService;
	
	public ActaController(ServicioExternalService servicioExternalService, SedeService sedeService, ExcelService excelService) {
		this.servicioExternalService = servicioExternalService;
		this.sedeService = sedeService;
		this.excelService = excelService;
	}
	
	@GetMapping("/getFormConteoActasSij") 
	public String getFormConteoActasSij(Model model) {
		List<ResponseSedeVO> sedes = this.sedeService.getSedes();
		model.addAttribute("sedes", sedes);
		return "vistas/ncpp/actas";  
	}
	
	@GetMapping("/getActasSij") 
	public @ResponseBody ResponseEntity<List<ResponseResumenAsistenteVO>> getConteoActasSij(String sede, String fechaInicio, String fechaFin, String estado) {
		LocalDateTime fecIni = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		LocalDateTime fecFin = LocalDate.parse(fechaFin, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		long dias = ChronoUnit.MONTHS.between(fecIni, fecFin);
		if(dias > 12) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de fechas no debe superar los 12 meses");
		}
		List<ResponseResumenAsistenteVO> responseResumenAsistenteVOS = this.servicioExternalService.getConteoActasSij(sede, fechaInicio, fechaFin, estado);
		return new ResponseEntity<>(responseResumenAsistenteVOS, HttpStatus.OK);  
	}
	
	@GetMapping("/getActaSij") 
	public void getActaSij(HttpServletResponse response, @RequestParam(value="sede") String sede, @RequestParam(value="descargo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
		LocalDateTime descargo, @RequestParam(value="documento") String documento) throws MessagingException, IOException {
		if(this.servicioExternalService.getActaSij(sede, descargo, documento)) {
			File archivo = new File(Constantes.RUTA_SERVIDOR_LOCAL + documento.concat(Constantes.EXTENSION_WORD));
			response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=" + documento.concat(Constantes.EXTENSION_WORD));
	        InputStream stream = new BufferedInputStream(new FileInputStream(archivo));
	        IOUtils.copy(stream, response.getOutputStream());
		}
		else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo descargar el documento");
	}
	
	@PostMapping(value = "download/pdfListaActas", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> employeeReport(@RequestParam(value="usuario") String usuario, @RequestParam(value="inicio") String inicio, 
			@RequestParam(value="fin") String fin, @RequestBody List<RequestActaVO> requestActaVO) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss");
		LocalDateTime fecInicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		LocalDateTime fecFin = LocalDate.parse(fin, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		
		ByteArrayInputStream bis = PdfListaActas.pdfListaActas(usuario, fecInicio, fecFin, requestActaVO);
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=REPORTE_ACTAS_" + usuario.replace(" ", "_") + LocalDateTime.now().format(formatter) + Constantes.EXTENSION_PDF);
	
		return ResponseEntity.ok().headers(headers).contentType
	               (MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
	}
	
	@PostMapping(value = "download/excelListaActas", produces = MediaType.ALL_VALUE)
	public void excelListaActas(@RequestParam(value="usuario") String usuario, @RequestParam(value="inicio") String inicio, 
			@RequestParam(value="fin") String fin, @RequestBody List<RequestActaVO> requestActaVO, HttpServletResponse response) throws IOException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime fecInicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		LocalDateTime fecFin = LocalDate.parse(fin, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		
		ByteArrayOutputStream jxlsOutStream = new ByteArrayOutputStream();
		
		Map<String, Object> data = new HashMap<>();
		data.put("usuario", usuario);
		data.put("fechaInicio", fecInicio.format(formatter));
		data.put("fechaFin", fecFin.format(formatter));
		data.put("actas", requestActaVO);

		this.excelService.generarDocumento(jxlsOutStream, Constantes.PLANTILLA_ACTAS, data);
		
		formatter = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss");	
		response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Reporte_Actas_" + usuario.replace(" ", "_") + LocalDateTime.now().format(formatter) + Constantes.EXTENSION_EXCEL);
		InputStream inputStream = new ByteArrayInputStream(jxlsOutStream.toByteArray());
		IOUtils.copy(inputStream, response.getOutputStream());

	}
	
}
