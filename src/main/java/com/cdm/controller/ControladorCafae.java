package com.cdm.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.config.Conexion;
import com.cdm.entities.AbogadoCriteria;
import com.cdm.entities.Parametro;
import com.cdm.entities.Secretario;
import com.cdm.entities.Usuario;
import com.cdm.entities.ValueCriteria;
import com.cdm.utils.ExcelArchivo;

@Controller
public class ControladorCafae {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
	JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarMySQLElecciones());
	
	@GetMapping("/liberacionCafae") 
	public String liberacionCafae(Model model, Principal principal) {
		return "vistas/liberacionCafae";  
	}
	
	@GetMapping("/escrutinioCafae") 
	public String escrutinioCafae(Model model, Principal principal) {
		return "vistas/escrutinioCafae";  
	}
	
	@GetMapping("/reporteCafae") 
	public String reporteCafae(Model model, Principal principal) {
		return "vistas/reporteCafae";  
	}
	
	@GetMapping("/repCafae") 
	public String repCafae(Model model, Principal principal) {
		String sql = "SELECT * FROM electores e WHERE NOT EXISTS (SELECT NULL FROM votos v WHERE v.s_dni = e.s_dni)";
		List<?> electores = this.jdbcTemplate2.queryForList(sql);
		model.addAttribute("electores", electores);
		return "vistas/repCafae";  
	}
	
	@GetMapping("/escrutinio") 
	public String escrutinio(Model model, Principal principal) {
		String sql = "SELECT l.s_lista, COUNT(*) as conteo FROM listas l INNER JOIN votos v ON v.id_lista = l.id_lista WHERE l.id_lista = 1\r\n" + 
				"UNION ALL \r\n" + 
				"SELECT l.s_lista, COUNT(*) as conteo FROM listas l INNER JOIN votos v ON v.id_lista = l.id_lista WHERE l.id_lista = 2";
		List<?> lista = this.jdbcTemplate2.queryForList(sql);
		sql = "SELECT CONCAT('TOTAL ELECTORES: ') AS titulo, COUNT(*) AS conteo FROM electores e UNION ALL \r\n" + 
				"SELECT CONCAT('TOTAL VOTOS: ') AS titulo, COUNT(*) AS conteo FROM votos v UNION ALL \r\n" + 
				"SELECT CONCAT('TOTAL ELECTORES SIN VOTO: ') AS titulo, COUNT(*) AS conteo FROM electores e WHERE NOT EXISTS (SELECT NULL FROM votos v WHERE v.s_dni = e.s_dni)";
		List<?> cantidad = this.jdbcTemplate2.queryForList(sql);
		sql= "select date_format(NOW(), '%d-%m-%Y %r') as fechaActual";
		String fecha = this.jdbcTemplate2.queryForObject(sql, String.class);
		model.addAttribute("listas", lista);
		model.addAttribute("cantidades", cantidad);
		model.addAttribute("fechaActual", fecha);
		return "vistas/escrutinio";  
	}
	
	@RequestMapping(value="/liberar", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> liberar(@RequestBody ValueCriteria vc, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String query = "update electores set n_intentos = 2 where s_dni = ?";
			this.jdbcTemplate2.update(query, vc.getId());
			map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/accederCafae", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> accederCafae(@RequestBody ValueCriteria vc, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		try {
			String sql = "select idperfil from usuarios where c_usuario = ?";
			int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
			
			if(perfil == 13) 
				sql = "select s_clave_usuario from configuracion where id_eleccion = 1";
			else if(perfil == 14) 
				sql = "select s_clave_coordinador from configuracion where id_eleccion = 1";
			
			String pass = this.jdbcTemplate2.queryForObject(sql, String.class);
			boolean acceso = bCryptPasswordEncoder.matches(vc.getId(), pass);		
			if(acceso) 
				map.put("Status", 200);
			else
				map.put("Status", 500);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/cambiarClaveCafae", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> cambiarClaveCafae(@RequestBody Parametro parametro, Principal principal) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		try {
			String sql = "select idperfil from usuarios where c_usuario = ?";
			int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
			
			if(perfil == 13) {
				sql = "select s_clave_usuario from configuracion where id_eleccion = 1";
				String pass = this.jdbcTemplate2.queryForObject(sql, String.class);
				boolean acceso = bCryptPasswordEncoder.matches(parametro.getCodigo(), pass);
				if(acceso) { 
					sql = "update configuracion set s_clave_usuario = ? where id_eleccion = 1";
					this.jdbcTemplate2.update(sql, bCryptPasswordEncoder.encode(parametro.getValor()));
					map.put("Status", 200);
				}
				else
					map.put("Status", 400);
			}
			else if(perfil == 14){
				sql = "select s_clave_coordinador from configuracion where id_eleccion = 1";
				String pass = this.jdbcTemplate2.queryForObject(sql, String.class);
				boolean acceso = bCryptPasswordEncoder.matches(parametro.getCodigo(), pass);
				if(acceso) { 
					sql = "update configuracion set s_clave_coordinador = ? where id_eleccion = 1";
					this.jdbcTemplate2.update(sql, bCryptPasswordEncoder.encode(parametro.getValor()));
					map.put("Status", 200);
				}
				else
					map.put("Status", 400);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@GetMapping("/download/electores_sin_voto.xlsx")
    public void downloadCsv(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=electores_sin_voto.xlsx");
        ByteArrayInputStream stream = ExcelArchivo.listaUsuarios(extraeDatos());
        IOUtils.copy(stream, response.getOutputStream());
    }
	
	private List<Usuario> extraeDatos(){
    	List<Usuario> usuarios = new ArrayList<Usuario>();
    	String sql = "SELECT * FROM electores e WHERE NOT EXISTS (SELECT NULL FROM votos v WHERE v.s_dni = e.s_dni)";
        List<Map<String, Object>> rows = jdbcTemplate2.queryForList(sql);
        
        for (Map row : rows) {
            Usuario usu = new Usuario();
            usu.setDni((String) row.get("s_dni"));
            usu.setNombres((String) row.get("s_nombres"));
            usuarios.add(usu);
        }
    	return usuarios;
    }
}
