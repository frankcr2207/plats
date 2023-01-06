package com.cdm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cdm.config.Conexion;

@Controller
public class ControladorDirectorio {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/getDirectory", method = RequestMethod.GET)
	public String lista(Model model, @RequestParam(value="nombres") String nombres) {
		String query = "select * from directorio where s_nombres like ? or s_apellidos like ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, "%"+ nombres +"%", "%"+ nombres +"%");
		model.addAttribute("lista", lista);
		return "vistas/listaDirectorio";
	}
	
	@RequestMapping(value="/getEspDir", method = RequestMethod.GET)
	public String listaXEsp(Model model, @RequestParam(value="varFam") String varFam, @RequestParam(value="varLab") String varLab, @RequestParam(value="varCiv") String varCiv, @RequestParam(value="varPen") String varPen, @RequestParam(value="varExt") String varExt, @RequestParam(value="varAdm") String varAdm, @RequestParam(value="varCon") String varCon) {
		String addFam="", addLab="", addCiv="", addPen="", addExt="", addAdm="", addCon="";
		if(!varFam.equals("0")) addFam = "'FA',"; else addFam = "";
		if(!varLab.equals("0")) addLab = "'LA',"; else addLab = "";
		if(!varCiv.equals("0")) addCiv = "'CI',"; else addCiv = "";
		if(!varPen.equals("0")) addPen = "'PE',"; else addPen = "";
		if(!varExt.equals("0")) addExt = "'ED',"; else addExt = "";
		if(!varAdm.equals("0")) addAdm = "'CA',"; else addAdm = "";
		if(!varCon.equals("0")) addCon = "'CO'"; else addCon = "";
		
		String condicion = addFam + addLab + addCiv + addPen + addExt + addAdm + addCon;
		String letra = condicion.substring(condicion.length()-1, condicion.length());
		if(letra.equals(",")) {
			condicion = condicion.substring(0, condicion.length()-1);
		}
		String query = "SELECT DISTINCT d.s_dni, d.s_nombres, d.s_apellidos FROM directorio d\r\n" + 
				"INNER JOIN abogado_especialidad ae ON ae.s_dni = d.s_dni \r\n" + 
				"WHERE ae.c_especialidad IN (" + condicion + ") ORDER BY d.s_apellidos ASC";

		List<?> lista = this.jdbcTemplate.queryForList(query);
		model.addAttribute("lista", lista);
		return "vistas/listaDirectorio";
	}
	
	@RequestMapping(value="/verDetalleDirectorio", method = RequestMethod.GET)
	public String detalle(Model model, @RequestParam(value="value") String dni) {
		String query = "select concat(d.s_nombres, ' ', d.s_apellidos) as nombresCompletos, d.*, c.s_colegio from directorio d inner join colegio c on c.n_id_colegio = d.id_colegio where d.s_dni = ?";
		List<?> abogado = this.jdbcTemplate.queryForList(query, dni);
		query = "select e.s_especialidad from abogado_especialidad ae inner join especialidad e on e.c_especialidad = ae.c_especialidad where ae.s_dni = ?";
		List<?> esp = this.jdbcTemplate.queryForList(query, dni);
		model.addAttribute("abogado", abogado);
		model.addAttribute("especialidades", esp);
		return "vistas/verDetalleDirectorio";
	}
	
	@GetMapping("/calculo") 
	public String calculo(Model model, Principal principal) {
		return "vistas/calculo";  
	}
}
