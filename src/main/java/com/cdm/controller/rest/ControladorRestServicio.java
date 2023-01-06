package com.cdm.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorRestServicio {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@GetMapping("/obtReporteServicio")
    public @ResponseBody List<?> obtReporteServicio(String inicio, String fin) {
    	String sql = "SELECT date_format(se.f_registro, '%d-%m-%Y %r') as registro, se.s_dni, se.s_nombres, se.s_apellidos, se.n_expediente, s.s_sede, i.x_nom_instancia, se.s_observacion, date_format(se.f_atencion, '%d-%m-%Y %r') as atencion, se.s_estado, se.s_respuesta, se.s_mensaje, se.s_respuesta\r\n" + 
    		"FROM servicio se INNER JOIN sede s ON s.c_sede = se.c_sede INNER JOIN instancia i ON i.c_instancia = se.c_instancia\r\n" + 
    		"WHERE se.f_registro >= ? AND se.f_registro <= ? ORDER BY se.f_registro ASC";
    	List<?> reporte = jdbcTemplate.queryForList(sql, inicio, fin);
    	return reporte;
    }
}
