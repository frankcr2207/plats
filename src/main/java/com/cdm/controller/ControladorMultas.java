package com.cdm.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.dao.EspecialidadDAO;
import com.cdm.dao.MultaDAO;
import com.cdm.dao.SedeDAO;
import com.cdm.service1.EspecialidadService;
import com.cdm.service1.MultaService;
import com.cdm.service1.SedeService;

@Controller
public class ControladorMultas {
	
	private SedeService sedeService;
	
	private EspecialidadService especialidadService;
	
	private MultaService multaService;
	
	public ControladorMultas(SedeService sedeService, EspecialidadService especialidadService,
		MultaService multaService) {
		this.sedeService = sedeService;
		this.especialidadService = especialidadService;
		this.multaService =multaService;
	}
	
	@GetMapping("/multas") 
	public String multas(Model model, Principal principal) {
		model.addAttribute("sedes", sedeService.getSedes());
		model.addAttribute("especialidades", especialidadService.getEspecialidades());
		return "vistas/multa/multas";  
	}
	
	@GetMapping("/multasCoor") 
	public String listaMultas(Model model, @RequestParam(required = false) String parametro, Principal principal) {
		model.addAttribute("multas", this.multaService.getMultasSJ(parametro));
		return "vistas/multa/listaMultas";  
	}
	
	@GetMapping("/detalleMulta") 
	public String detalleMulta(Model model, Principal principal, int id) {
		model.addAttribute("detalle", this.multaService.getMulta(id));
		return "vistas/multa/detalleMulta";  
	}
	
}
