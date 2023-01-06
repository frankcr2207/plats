package com.cdm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.config.Conexion;
import com.cdm.entities.Respuesta;
import com.cdm.entities.Persona;
import com.cdm.entities.Pregunta;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class WebServiceConcurso {
	
	Conexion con = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(con.ConectarMySQLConcurso());
	
	@RequestMapping(value = "/wsRestLoadFirst", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Pregunta restLoadFirst(@RequestBody Respuesta rpta) {
		Pregunta pregunta = new Pregunta();
		
		String sql = "SELECT MIN(p.n_id_pregunta) as primera FROM preguntas p WHERE p.n_id_examen = ?";
		int primera = this.jdbcTemplate.queryForObject(sql, int.class, rpta.getExamen());
		
		sql = "SELECT n_id_pregunta, s_pregunta FROM preguntas WHERE n_id_pregunta = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, primera);
        
        for (Map row : rows) {
        	pregunta.setId((int) row.get("n_id_pregunta"));
        	pregunta.setNombre((String) row.get("s_pregunta"));
        }
        
        sql = "SELECT a.n_id_alternativa, a.s_alternativa FROM alternativas a WHERE a.n_id_pregunta = ?";
        List<Map<String, Object>> alts = jdbcTemplate.queryForList(sql, pregunta.getId());
        List<Integer> idAlternativas = new ArrayList<Integer>();
        List<String> alternativas = new ArrayList<String>();
        for (Map alt : alts) {
            idAlternativas.add((int) alt.get("n_id_alternativa"));
            alternativas.add((String) alt.get("s_alternativa"));
        }
        pregunta.setIdAlternativas(idAlternativas);
        pregunta.setAlternativas(alternativas);
        pregunta.setBoton1(0);
        pregunta.setBoton2(2);
		
		return pregunta;
    }
	
	@RequestMapping(value = "/wsRestLoad", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Pregunta wsRestLoadOption(@RequestBody Respuesta rpta) {
		
		String sql;
		if(rpta.getEleccion() != 0) {
			sql = "DELETE FROM respuesta_postulante WHERE s_dni = ? AND n_id_examen = ? AND n_id_pregunta = ?";
			this.jdbcTemplate.update(sql, rpta.getDni(), rpta.getExamen(), rpta.getPregunta());
			sql = "INSERT respuesta_postulante (s_dni, n_id_examen, n_id_cargo, n_id_pregunta, n_id_alternativa, f_respuesta) VALUES (?,?,0,?,?,NOW())";
			this.jdbcTemplate.update(sql, rpta.getDni(), rpta.getExamen(), rpta.getPregunta(), rpta.getEleccion());
		}
		
		Pregunta pregunta = new Pregunta();
		
		int question = 0;
		
		if(rpta.getMovimiento() == 1) {
			sql = "SELECT p.n_id_pregunta FROM preguntas p WHERE p.n_id_pregunta = (SELECT MAX(pr.n_id_pregunta) FROM preguntas pr WHERE pr.n_id_pregunta < ? AND pr.n_id_examen = ?)";
			question = this.jdbcTemplate.queryForObject(sql, int.class, rpta.getPregunta(), rpta.getExamen());
		}
		else if (rpta.getMovimiento() == 2) {
			sql = "SELECT p.n_id_pregunta FROM preguntas p WHERE p.n_id_pregunta = (SELECT MIN(pr.n_id_pregunta) FROM preguntas pr WHERE pr.n_id_pregunta > ? AND pr.n_id_examen = ?)";
			question = this.jdbcTemplate.queryForObject(sql, int.class, rpta.getPregunta(), rpta.getExamen());
		}
		//faltaria capturar la alternativa guardada
		int rptaPost = 0;
		sql = "SELECT a.n_id_alternativa FROM respuesta_postulante a WHERE a.s_dni = ? AND a.n_id_examen = ? AND a.n_id_pregunta = ? LIMIT 1";
		List<?> list = this.jdbcTemplate.queryForList(sql, rpta.getDni(), rpta.getExamen(), question);
		if(!list.isEmpty())
			rptaPost = this.jdbcTemplate.queryForObject(sql, int.class, rpta.getDni(), rpta.getExamen(), question);
		
		sql = "SELECT n_id_pregunta, s_pregunta FROM preguntas WHERE n_id_pregunta = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, question);
        
        for (Map row : rows) {
        	pregunta.setId((int) row.get("n_id_pregunta"));
        	pregunta.setNombre((String) row.get("s_pregunta"));
        }
        
        sql = "SELECT a.n_id_alternativa, a.s_alternativa FROM alternativas a WHERE a.n_id_pregunta = ?";
        List<Map<String, Object>> alts = jdbcTemplate.queryForList(sql, pregunta.getId());
        
        List<Integer> idAlternativas = new ArrayList<Integer>();
        List<String> alternativas = new ArrayList<String>();
        List<String> respuestas = new ArrayList<String>();
        for (Map alt : alts) {
        	if((int) alt.get("n_id_alternativa") == rptaPost)
        		respuestas.add("checked");
        	else
        		respuestas.add("");
        	
            idAlternativas.add((int) alt.get("n_id_alternativa"));
            alternativas.add((String) alt.get("s_alternativa"));
        }
        pregunta.setIdAlternativas(idAlternativas);
        pregunta.setAlternativas(alternativas);
        pregunta.setRespuesta(respuestas);
        
        List<?> lista = null;
        if(rpta.getMovimiento() == 1) {
        	sql = "SELECT p.n_id_pregunta FROM preguntas p WHERE p.n_id_pregunta = (SELECT MAX(pr.n_id_pregunta) FROM preguntas pr WHERE pr.n_id_pregunta < ? AND pr.n_id_examen = ?)";
        	lista = this.jdbcTemplate.queryForList(sql, question, rpta.getExamen());
        	if(lista.isEmpty()) 
            	pregunta.setBoton1(0);
            else
            	pregunta.setBoton1(1);
        	pregunta.setBoton2(2);
        }
        else if(rpta.getMovimiento() == 2){
        	sql = "SELECT p.n_id_pregunta FROM preguntas p WHERE p.n_id_pregunta = (SELECT MIN(pr.n_id_pregunta) FROM preguntas pr WHERE pr.n_id_pregunta > ? AND pr.n_id_examen = ?)";
        	lista = this.jdbcTemplate.queryForList(sql, question, rpta.getExamen());
        	if(lista.isEmpty())
            	pregunta.setBoton2(3);
            else
            	pregunta.setBoton2(2);
        	pregunta.setBoton1(1);
        }
        
		return pregunta;
    }
	
	@RequestMapping(value = "/wsRestEnd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Pregunta wsRestEnd(@RequestBody Respuesta rpta) {
		String sql = "";
		
		if(rpta.getEleccion() != 0) {
			sql = "DELETE FROM respuesta_postulante WHERE s_dni = ? AND n_id_examen = ? AND n_id_pregunta = ?";
			this.jdbcTemplate.update(sql, rpta.getDni(), rpta.getExamen(), rpta.getPregunta());
			sql = "INSERT respuesta_postulante (s_dni, n_id_examen, n_id_cargo, n_id_pregunta, n_id_alternativa, f_respuesta) VALUES (?,?,0,?,?,NOW())";
			this.jdbcTemplate.update(sql, rpta.getDni(), rpta.getExamen(), rpta.getPregunta(), rpta.getEleccion());
		}
		
		sql = "UPDATE postulantes SET n_intentos = 0 WHERE s_dni = ?";
		this.jdbcTemplate.update(sql, rpta.getDni());
		
		Pregunta pregunta = new Pregunta();
		pregunta.setId(1);
		return pregunta;
    }
	
	@RequestMapping(value = "/wsRestAccess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String,Object> wsRestAccess(@RequestBody Persona persona) {
		String sql = "";
		Map<String,Object> objeto = new HashMap<String,Object>();
		sql = "SELECT concat(p.s_nombres, ' ', p.s_apellidos) as nombresCompletos, p.s_ingreso, p.n_intentos, c.s_cargo, e.n_id_examen FROM postulantes p INNER JOIN r_postulante_cargo pc ON pc.s_dni = p.s_dni INNER JOIN cargos c ON c.n_id_cargo = pc.n_id_cargo INNER JOIN r_cargo_examen ce ON ce.n_id_cargo = c.n_id_cargo INNER JOIN examenes e ON e.n_id_examen = ce.n_id_examen WHERE p.s_dni = ? and p.n_anio = ? and p.n_codigo = ? limit 1";
		List<Map<String, Object>> lista = this.jdbcTemplate.queryForList(sql, persona.getDni(), persona.getNacimiento(), persona.getCodigo());
		
		if(lista.isEmpty())
			objeto.put("status", "X");
		else {
	        for (Map row : lista) {
	        	String ingreso = (String) row.get("s_ingreso");
	        	if(ingreso.equals("N"))
	        		objeto.put("status", "N");
	        	else {
	        		if((int) row.get("n_intentos") == 0)
	        			objeto.put("status", "I");
	        		else {
	        			sql = "update postulantes p set p.s_ingreso = 'N' where p.s_dni = ?";
	    	    		this.jdbcTemplate.update(sql, persona.getDni());
	        			objeto.put("status", "OK");
	        		}
	        	}
	        	objeto.put("dni", persona.getDni());
        		objeto.put("nombres", (String) row.get("nombresCompletos"));
        		objeto.put("cargo", (String) row.get("s_cargo"));
        		objeto.put("examen", (int) row.get("n_id_examen"));
	        }
		}
			
		return objeto;
    }
	
	@RequestMapping(value = "/wsRestTime", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String,Object> wsRestTime(@RequestBody Persona persona) {
		String sql = "";
		Map<String,Object> objeto = new HashMap<String,Object>();
		/*sql = "SELECT * FROM postulantes p WHERE p.s_dni = ? limit 1";
		List<Map<String, Object>> lista = this.jdbcTemplate.queryForList(sql, persona.getDni());
		for (Map row : lista) {
	       	String ip = (String) row.get("s_ip");
	       	if(ip.equals("S")) {
	       		sql = "update postulantes p set p.s_ip = 'N' where p.s_dni = ?";
	    		this.jdbcTemplate.update(sql, persona.getDni());
	       		objeto.put("ip", "OK");
	       	}
	       	else 
	       		objeto.put("ip", "X");
	    }*/
		
		List<Map<String, Object>> lista = null;
		sql = "select * from examenes where f_inicio <= now() and n_id_examen = ?";
		lista = this.jdbcTemplate.queryForList(sql, persona.getCodigo());
		if(lista.isEmpty())
			objeto.put("tiempo", 0);
		else {
			lista = null;
			sql = "SELECT TIMESTAMPDIFF(MINUTE, NOW(), e.f_fin) as minutos, 60 - DATE_FORMAT(NOW(), '%s') as segundos FROM examenes e WHERE e.n_id_examen = ?";
			lista = this.jdbcTemplate.queryForList(sql, persona.getCodigo());
			for (Map row : lista) {
				long minutos = (long) row.get("minutos");
				double segundos = (double) row.get("segundos");
	        	if(minutos < 0 || (minutos == 0 && segundos < 2)) {
	        		objeto.put("minutos", 0);
	        		objeto.put("segundos", 0);
	        		objeto.put("tiempo", -1);
	        	}
	        	else {
	        		objeto.put("minutos", (long) row.get("minutos"));
	        		objeto.put("segundos", (double) row.get("segundos"));
	        		objeto.put("tiempo", 1);
	        	}
	        }
		}
			
		return objeto;
    }
	
	@RequestMapping(value = "/wsRestAuth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Pregunta wsRestAuth(@RequestBody Respuesta rpta) {
		Pregunta pregunta = new Pregunta();
		
		return pregunta;
    }
	
}
