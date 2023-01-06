package com.cdm.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.config.Conexion;

@Controller
public class ControladorAudiencias {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
	JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
	
	public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }  
	
	public String nombreCompleto(Principal principal) {
        String user = principal.getName();
        String sql = "select concat(s_nombres,' ',s_apellido_paterno,' ',s_apellido_materno) as nombres from usuarios where c_usuario = ?";
        String _nombre = this.jdbcTemplate.queryForObject(sql, String.class, user);
        return _nombre;
    }
	
	@GetMapping("/AUDIENCIAS") 
	public String meet(Model model, Principal principal) {

		String _nombre = this.nombreCompleto(principal);
		model.addAttribute("nombre",_nombre);
		model.addAttribute("tipo", "AUDIENCIAS VIRTUALES GOOGLE MEET");
		String sql = "select a.id_audiencia, a.n_expediente, date_format(a.fecha_audiencia, '%d-%m-%Y %r') as fechaAudiencia, date_format(a.fecha_registro, '%d-%m-%Y %r') as fechaRegistro, s.s_sede, a.s_instancia, a.s_estado from audiencias a inner join sede s on s.c_sede = a.c_sede where a.c_usuario = ? and a.fecha_audiencia >= DATE_SUB(NOW(), INTERVAL 5 day) order by a.fecha_audiencia desc";
		List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
		sql = "SELECT c_sede, s_sede FROM sede ORDER BY prelacion ASC ";
		List<?> sedes = this.jdbcTemplate.queryForList(sql);
		model.addAttribute("audiencias", lista);
		model.addAttribute("sedes", sedes);
		model.addAttribute("formulario", "AUDIENCIAS");
		return "vistas/audiencias";  
	}
	
	@RequestMapping(value = "/detalleAud", method = RequestMethod.GET)
	public String verAud(Model model, @RequestParam(value="id") String id, Principal principal) {
		String _nombre = this.nombreCompleto(principal);
		String sql;
		sql = "select *, date_format(fecha_audiencia, '%d-%m-%Y %r') as fechaAudiencia, date_format(fecha_registro, '%d-%m-%Y %r') as fechaRegistro, s_estado from audiencias where id_audiencia = ?";
		List<?> lista = this.jdbcTemplate.queryForList(sql, id);
		model.addAttribute("nombre",_nombre);
		model.addAttribute("audiencia", lista);

		return "vistas/detalleAudiencia";  
	}
    
    @RequestMapping(value="/guardarAudiencia", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> guardarAudiencia(HttpServletResponse response, @RequestParam(value="exp") String exp, @RequestParam(value="sede") String sede, @RequestParam(value="ins") String ins, @RequestParam(value="tip") String tip, @RequestParam(value="fecha") String fecha,  @RequestParam(value="par") String par, @RequestParam(value="par2") String par2, @RequestParam(value="jueces") String jueces, @RequestParam(value="del") String del, @RequestParam(value="ini") String ini, @RequestParam(value="reserva") String reserva, @RequestParam(value="enl") String enl, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String ip = getClientIp();
		try {
			if(exp.equals("") || sede.equals("0") || ins.equals("") || tip.equals("") || fecha.equals("") || par.equals("") || par2.equals("") || del.equals("")) {
				map.put("Status", 400);
			}
			else {
				
				enl = enl.trim();
				if(enl.length() != 0) {
					String ver = enl.substring(0, 5);
					if(!ver.equals("https")) {
						enl = "https://" + enl;
					}
				}
				String query = "insert into audiencias (n_expediente, c_sede, s_instancia, s_tipo_audiencia, fecha_audiencia, s_imputado, s_agraviado, s_jueces, s_delito, s_reservado, s_link_audiencia, ip_audiencia, fecha_registro, c_usuario, s_estado, c_especialidad) values (?,?,?,?,?,?,?,?,?,?,?,?,now(),?,'P','PE')";
				this.jdbcTemplate.update(query, exp, sede, ins, tip, ini, par, par2, jueces, del, reserva, enl, ip, principal.getName());
				map.put("Status", 200);
			}
		}
		catch (Exception ex){
			map.put("Status", 500);
			System.out.print(ex.getMessage());
		}
		return map;
	}
	
	@RequestMapping(value="/actualizarAudiencia", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> actualizarAudiencia(HttpServletResponse response,  @RequestParam(value="codAud") String codAud, @RequestParam(value="enl") String enl, @RequestParam(value="fechaAntigua") String fechaAntigua, @RequestParam(value="fechaNueva") String fechaNueva, @RequestParam(value="estadoRes") String res, @RequestParam(value="estadoAud") String estAud, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String ip = getClientIp(), query;
		try {
			enl = enl.trim();
			if(enl.length() != 0) {
				String ver = enl.substring(0, 5);
				if(!ver.equals("https")) {
					enl = "https://" + enl;
				}
			}
			if(fechaNueva.equals("")) {
				query = "update audiencias set s_link_audiencia = ?, s_reservado = ?, s_estado = ? where id_audiencia = ?";
				this.jdbcTemplate.update(query, enl, res, estAud, codAud);
			}
			else {
				query = "update audiencias set s_link_audiencia = ?, fecha_audiencia = ?, s_reservado= ?, s_estado = ? where id_audiencia = ?";
				this.jdbcTemplate.update(query, enl, fechaNueva, res, estAud, codAud);
			}
			
			map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	
}
