package com.cdm.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cdm.service1.SedeService;
import com.cdm.service2.SunarpSolicitudService;

@Controller
public class SunarpController {

	private SunarpSolicitudService sunarpSolicitudService;
	
	private SedeService sedeService;
	
	public SunarpController(SunarpSolicitudService sunarpSolicitudService, SedeService sedeService) {
		this.sunarpSolicitudService = sunarpSolicitudService;
		this.sedeService = sedeService;
	}
	
	@GetMapping("/SUNARP")
	public String getSunarpSolicitudes(Model model, String sede) {
		model.addAttribute("solicitudes", this.sunarpSolicitudService.getSunarpSolicitudes(sede));
		model.addAttribute("sedes", this.sedeService.getSedes());
		return "vistas/sunarp/listaSunarp";
	}
	
	@GetMapping("/detalleSunarp")
	public String detalleSunarp(Model model, Integer id) {
		model.addAttribute("solicitud", this.sunarpSolicitudService.getSunarpSolicitud(id));
		model.addAttribute("anio", LocalDateTime.now().getYear());
		model.addAttribute("sesion", SecurityContextHolder.getContext().getAuthentication().getName());
		return "vistas/sunarp/detalleSunarp";
	}
}
