package com.cdm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.service1.CdgDocumentoService;
import com.cdm.service1.SedeService;

@Controller
public class CdgController {
	
	private CdgDocumentoService cdgDocumentosService;
	
	private SedeService sedeService;
	
	public CdgController(CdgDocumentoService cdgDocumentosService, SedeService sedeService) {
		this.cdgDocumentosService = cdgDocumentosService;
		this.sedeService = sedeService;
	}
	
	@GetMapping("/MESAPARTES")
	public String getInpeSolicitudes(Model model, Principal principal, @RequestParam(defaultValue = "") String sede, 
			 @RequestParam(defaultValue = "") String fecInicio, @RequestParam(defaultValue = "") String fecFin, 
			 @RequestParam(defaultValue = "") String parametro) {
		try {
			List<ResponseCdgDocumentoVO> responseCdgDocumentoVOS = this.cdgDocumentosService.getCdgDocumentos(
					principal.getName(), sede, fecInicio, fecFin, parametro);
			model.addAttribute("tipo", "MPE - DOCUMENTOS PENDIENTES");
			model.addAttribute("documentos", responseCdgDocumentoVOS);
			model.addAttribute("sedes", this.sedeService.getSedes());
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/cdgdocumento/listaCdgDocumento";  
	}
	
	@GetMapping("/URGENTES")
	public String urgentes(Model model, Principal principal) {
		try {
			model.addAttribute("documentos", this.cdgDocumentosService.getCdgDocumentosUrgentes());
			model.addAttribute("tipo", "MPE - URGENTES");
			model.addAttribute("formulario", "URGENTES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/cdgdocumento/listaCdgDocumento";
	}
}
