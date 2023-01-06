package com.cdm.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.config.Conexion;
import com.cdm.entities.AudienciaCriteria;
import com.cdm.entities.Proceso;
import com.cdm.entities.Secretario;

@Controller
public class ControladorInhibicion {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }
	
	@GetMapping("/expedientes") 
	public String directorio(Model model, Principal principal) {
		model.addAttribute("tipo", "REDISTRIBUCION DE EXPEDIENTES (Inhibiciones u otros)");
		String sql = "SELECT CONCAT(i.n_numero, '-', i.n_anio, '-', i.n_cuaderno) AS expediente, s.s_sede, ins.x_nom_instancia, DATE_FORMAT(i.f_sistema, '%d-%m-%Y %r') as fechaRegistro, se.s_sede as sedeDestino, inst.x_nom_instancia as insDestino, i.s_observacion, concat(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombresCompletos  \r\n" + 
			"FROM inhibicion i INNER JOIN sede s ON s.c_sede = i.c_sede INNER JOIN instancia ins ON ins.c_instancia = i.c_instancia\r\n" + 
			"INNER JOIN sede se ON se.c_sede = i.c_sede_destino INNER JOIN instancia inst ON inst.c_instancia = i.c_instancia_destino\r\n" + 
			"INNER JOIN usuarios u ON u.c_usuario = i.c_usuario order by i.f_sistema desc";
		List<?> lista = this.jdbcTemplate.queryForList(sql);
		
		model.addAttribute("lista", lista);
		model.addAttribute("formulario", "INHIBICIONES");
		return "vistas/reasignacion";  
	}
	
	@GetMapping("/INHIBICIONES") 
	public String inhibiciones(Model model, Principal principal) {
		model.addAttribute("tipo", "REDISTRIBUCION DE EXPEDIENTES (Inhibiciones u otros)");
		String sql = "SELECT CONCAT(i.n_numero, '-', i.n_anio, '-', i.n_cuaderno) AS expediente, s.s_sede, ins.x_nom_instancia, DATE_FORMAT(i.f_sistema, '%d-%m-%Y %r') as fechaRegistro, se.s_sede as sedeDestino, inst.x_nom_instancia as insDestino, i.s_observacion FROM inhibicion i \r\n" + 
			"INNER JOIN sede s ON s.c_sede = i.c_sede INNER JOIN instancia ins ON ins.c_instancia = i.c_instancia\r\n" + 
			"INNER JOIN sede se ON se.c_sede = i.c_sede_destino INNER JOIN instancia inst ON inst.c_instancia = i.c_instancia_destino\r\n" + 
			"WHERE i.c_usuario = ? order by i.f_sistema desc";
		List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
		sql = "select c_sede from usuarios where c_usuario = ?";
        String sede = this.jdbcTemplate.queryForObject(sql, String.class, principal.getName());
		sql = "SELECT c_instancia, x_nom_instancia FROM instancia where c_sede = ? and s_penal = 'S' and s_tipo = 'JIP' ORDER BY x_nom_instancia asc";
		List<?> instancias = this.jdbcTemplate.queryForList(sql, sede);
		model.addAttribute("lista", lista);
		model.addAttribute("sedeOrigen", sede);
		model.addAttribute("instancias", instancias);
		model.addAttribute("formulario", "INHIBICIONES");
		return "vistas/inhibicion";  
	}
	
	@RequestMapping(value="/guardarInhibicion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Proceso guardarAudiencia(@RequestBody AudienciaCriteria aud, Principal principal) {
		String ip = getClientIp();
		Proceso json = new Proceso();
		try {

			String query;
			query = "SELECT i.c_instancia, i.x_nom_instancia, s.c_sede, s.s_sede FROM instancia i INNER JOIN sede s ON s.c_sede = i.c_sede WHERE i.s_aleatorio = 'S' ORDER BY RAND() LIMIT 1;";
			List<?> lista = this.jdbcTemplate.queryForList(query);
			
			if(lista.isEmpty()) {
				String sql = "UPDATE instancia SET s_aleatorio = 'S' WHERE s_aleatorio = 'N'";
				this.jdbcTemplate.update(sql);
			}
			
	        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
	        Proceso proceso = new Proceso();
	        for (Map row : rows) {
	        	proceso.setId_instancia((String) row.get("c_instancia"));
	        	proceso.setInstancia((String) row.get("x_nom_instancia"));
	        	proceso.setId_sede((String) row.get("c_sede"));
	        	proceso.setSede((String) row.get("s_sede"));
	        }
	        
	        query = "UPDATE instancia SET s_aleatorio = 'N' WHERE c_instancia = ?";
			this.jdbcTemplate.update(query, proceso.getId_instancia());
			
			query = "insert into inhibicion (n_numero, n_anio, n_cuaderno, c_sede, c_instancia, s_observacion, c_usuario, s_ip, f_sistema, c_sede_destino, c_instancia_destino, s_estado) values (?,?,?,?,?,?,?,?,now(),?,?,'E')";
			this.jdbcTemplate.update(query, aud.getNumero(), aud.getAnio(), aud.getCuaderno(), aud.getSede(), aud.getInstancia(), aud.getFecha().toUpperCase(), principal.getName(), ip, proceso.getId_sede(), proceso.getId_instancia());
			json.setEstado("ok");
			json.setSede(proceso.getSede());
			json.setInstancia(proceso.getInstancia());
		}
		catch (Exception ex){
			json.setEstado("err");
			System.out.print(ex.getMessage());
		}
		return json;
	}
}
