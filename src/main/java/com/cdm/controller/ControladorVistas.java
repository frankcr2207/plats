package com.cdm.controller;

import java.security.Principal;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

import javax.servlet.http.HttpServletRequest;


import com.cdm.config.Conexion;
import com.cdm.entities.Permiso;
import com.cdm.entities.Usuario;

@Controller
public class ControladorVistas {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
    
    String query = "call `obtSolicitudesCDM`(?,?)";
    
    String querySybase = "  SELECT instancia_expediente.n_unico, instancia_expediente.n_incidente, expediente.x_formato\r\n" + 
    		"   FROM instancia_expediente NoHoldLock,instancia NoHoldLock, expediente NoHoldLock,   \r\n" + 
    		"   WHERE ( instancia.c_distrito = instancia_expediente.c_distrito ) and  \r\n" + 
    		"         ( instancia.c_provincia = instancia_expediente.c_provincia ) and  \r\n" + 
    		"         ( instancia.c_instancia = instancia_expediente.c_instancia ) and  \r\n" + 
    		"         ( instancia_expediente.n_unico = expediente.n_unico ) and  \r\n" + 
    		"         ( instancia_expediente.n_incidente = expediente.n_incidente ) and  \r\n" + 
    		"          ( ( instancia_expediente.l_ultimo = 'S' ) AND  \r\n" + 
    		"         ( instancia_expediente.l_ultimo_c_org = 'S' ) AND  \r\n" + 
    		"         ( instancia_expediente.n_expediente = 100 ) AND  \r\n" + 
    		"         ( instancia_expediente.n_ano = 2019) AND  \r\n" + 
    		"         ( instancia_expediente.n_incidente = 0) AND  \r\n" + 
    		"         ( instancia_expediente.c_instancia = 'IM1' ) AND  \r\n" + 
    		"         ( instancia_expediente.c_especialidad = 'PE' )\r\n" + 
    		"AND           ( instancia.c_org_jurisd = '03' ) )   ";
    

    @SuppressWarnings("unused")
	private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }    
    //----------------------------------------------------------------------------------------------------------------------------------
    
	@RequestMapping("/")
	public String showIndexPage(){
		return "index";  
	}
	
	
	@GetMapping("/principal") 
	public String principal(Model model, Principal principal) {
		
		String sql = "SELECT (select s_nombres from usuarios where c_usuario = ?) as nombresCompletos, (select idperfil from usuarios where c_usuario = ?) AS perfil, (select c_especialidad from usuarios where c_usuario = ?) AS especialidad, \r\n" + 
				"(select count(*) from sedeorganousuario where c_usuario = ? and n_activo = 1) AS cdg, (select count(*) from r_usuario_cdm where c_usuario = ?) AS cdm";
		List<Map<String, Object>> alts = jdbcTemplate.queryForList(sql, principal.getName(), principal.getName(), principal.getName(), principal.getName(), principal.getName());
	    Permiso permiso = new Permiso();
        for (Map alt : alts) {
        	permiso.setNombres((String) alt.get("nombresCompletos"));
        	permiso.setPerfil((int) alt.get("perfil"));
        	permiso.setEspecialidad((String) alt.get("especialidad"));
        	Integer cdm = new Integer(String.valueOf(alt.get("cdm")));
        	Integer cdg = new Integer(String.valueOf(alt.get("cdg")));
        	permiso.setCdm(cdm);
        	permiso.setCdg(cdg);
        }
        model.addAttribute("nombre", permiso.getNombres());
        model.addAttribute("cargaCDG", permiso.getCdg());
		model.addAttribute("cargaCDM", permiso.getCdm());
		model.addAttribute("sesionRole", permiso.getPerfil());
		model.addAttribute("especialidad", permiso.getEspecialidad());
        
		return "vistas/principal";  
	}
	
	@GetMapping("/redistribucion") 
	public String redistribucion(Model model, Principal principal) {
		return "vistas/redistribucion";  
	}
	
	@GetMapping("/directorio") 
	public String directorio(Model model, Principal principal) {
		return "vistas/directorio";  
	}
	
	@GetMapping("/control") 
	public String control(Model model, Principal principal) {
		return "vistas/control";  
	}
	
	@GetMapping("/DEVOLUCION") 
	public String devolucion(Model model, Principal principal) {
		try {
			int tipoSolicitud = 1;
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("tipo", "DEVOLUCIÓN DE ANEXOS (Etapa postulatoria)");
			model.addAttribute("formulario", "DEVOLUCION");
			model.addAttribute("solicitudes", lista);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/listaObjetos";  
	}
	
	@GetMapping("/EXPEDICION") 
	public String expedicion(Model model, Principal principal) {
		try {
			int tipoSolicitud = 2;
			model.addAttribute("tipo", "EXPEDICIÓN DE PARTES JUDICIALES");
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "EXPEDICION");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	@GetMapping("/LECTURA") 
	public String lectura(Model model, Principal principal) {
		try {
			int tipoSolicitud = 4;
			model.addAttribute("tipo", "CITA PARA LECTURA DE EXPEDIENTES");
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "LECTURA");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	@GetMapping("/COPIAS") 
	public String copias(Model model, Principal principal) {
		try {
			int tipoSolicitud = 3;
			model.addAttribute("tipo", "SOLICITUD DE COPIAS SIMPLES y/o CERTIFICADAS");
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "COPIAS");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	@GetMapping("/ENDOSE") 
	public String endose(Model model, Principal principal) {
		try {
			int tipoSolicitud = 5;
			model.addAttribute("tipo", "COBRO Y/O ENDOSE DE CUPONES JUDICIALES");
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "ENDOSE");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	@GetMapping("/DOCUMENTAL") 
	public String documental(Model model, Principal principal) {
		try {
			int tipoSolicitud = 6;
			model.addAttribute("tipo", "DOCUMENTAL (Oficios, constancias, etc)");
			List<?> lista = this.jdbcTemplate.queryForList(query, principal.getName(), tipoSolicitud);
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "DOCUMENTAL");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}

	@GetMapping("/DERIVADOS") 
	public String bandejaCopias(Model model, Principal principal) {
		try {
			int tipoSolicitud = 5;
			String sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, i.x_nom_instancia, sc.n_expediente, date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, f.form,\r\n" + 
					"c.s_cdm, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado FROM solicitudes_cdm sc INNER JOIN resolucion d ON d.id_der_cdm = sc.n_id\r\n" + 
					"INNER JOIN instancia i ON i.c_instancia = sc.c_instancia INNER JOIN formulario f ON f.n_id_formulario = sc.n_id_formulario\r\n" + 
					"INNER JOIN cdm c ON c.n_id_cdm = sc.n_id_cdm WHERE d.c_usuario = ? AND sc.s_estado IN ('D','R') ORDER BY d.f_fecha_derivacion ASC;";
			model.addAttribute("tipo", "SOLICITUDES - Derivadas de CDM");
			List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			model.addAttribute("solicitudes", lista);
			model.addAttribute("formulario", "DERIVADOS");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	
	
	@GetMapping("/PENDIENTES") 
	public String pendientes(Model model, Principal principal) {
		try {
			String sql = "call `obtPendientesCDM`(?)";
			List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			model.addAttribute("solicitudes", lista);
			model.addAttribute("tipo", "MIS TAREAS PENDIENTES DE ATENCIÓN");
			model.addAttribute("formulario", "PENDIENTES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	
	@GetMapping("/PENDIENTESCDM") 
	public String pendientesCDM(Model model, Principal principal) {
		try {
			String sql = "call `obtPendientesAdmCDM`(?)";
			List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			model.addAttribute("solicitudes", lista);
			model.addAttribute("tipo", "PENDIENTES DE ATENCIÓN");
			model.addAttribute("formulario", "PENDIENTESCDM");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaObjetos";  
	}
	
	@RequestMapping(value = "/buscarSolicitudNombre", method = RequestMethod.GET)
	public String buscarSolicitudNombre(Model model, @RequestParam(value="formulario") String formulario, @RequestParam(value="nombres") String nombres, Principal principal) {
		String sql;
		List<?> lista;
		String tipo;
		if(formulario.equals("PENDIENTES")) {	
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, cdm c, instancia i, usuarios u \r\n" + 
					"WHERE sc.n_id_cdm=ruc.n_id_cdm AND sc.n_id_formulario=f.n_id_formulario AND c.n_id_cdm = sc.n_id_cdm AND i.c_instancia = sc.c_instancia\r\n" + 
					"AND ruc.c_usuario = ? AND sc.c_usuario = ? AND (sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) AND u.c_usuario = sc.c_usuario \r\n" + 
					"ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%");
			tipo = "RESULTADOS DE: " + nombres.toUpperCase();
		}
		else if(formulario.equals("PENDIENTESCDM")) {	
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form, \r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente \r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, cdm c, instancia i, usuarios u \r\n" + 
					"WHERE sc.n_id_cdm=ruc.n_id_cdm AND i.c_instancia = sc.c_instancia \r\n" + 
					"AND sc.n_id_formulario=f.n_id_formulario AND c.n_id_cdm = sc.n_id_cdm \r\n" + 
					"AND ruc.c_usuario = ? AND u.c_usuario = sc.c_usuario \r\n" + 
					"AND (sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%");
			tipo = "RESULTADOS DE: " + nombres.toUpperCase();
		}
		else{
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, resolucion r, cdm c, instancia i, formulario f, usuarios u\r\n" + 
					"WHERE r.id_der_cdm = sc.n_id AND c.n_id_cdm = sc.n_id_cdm AND i.c_instancia = sc.c_instancia\r\n" + 
					"AND f.n_id_formulario = sc.n_id_formulario AND u.c_usuario = r.c_usuario AND r.c_usuario = ? AND (sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) \r\n" + 
					"ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%");
			tipo = "RESULTADOS DE: " + nombres.toUpperCase();
		}
		model.addAttribute("tipo", tipo);
		model.addAttribute("solicitudes", lista);
		model.addAttribute("formulario", formulario);
		return "vistas/listaObjetos";  
	}
	
	@RequestMapping(value = "/buscarSolicitudFecha", method = RequestMethod.GET)
	public String buscarSolicitudFecha(Model model, @RequestParam(value="formulario") String formulario, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, Principal principal) {
		String sql;
		List<?> lista;
		String tipo;
		if(formulario.equals("PENDIENTES")) {
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, cdm c, instancia i, usuarios u \r\n" + 
					"WHERE sc.n_id_cdm=ruc.n_id_cdm AND sc.n_id_formulario=f.n_id_formulario AND c.n_id_cdm = sc.n_id_cdm AND u.c_usuario = sc.c_usuario \r\n" + 
					"AND i.c_instancia = sc.c_instancia AND ruc.c_usuario = ? AND sc.c_usuario = ? AND DATE(sc.f_fecha_atendido) BETWEEN ? AND ?\r\n" + 
					"ORDER BY sc.f_fecha_atendido ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), principal.getName(), inicio, fin);
			tipo = "RESULTADOS DESDE " + inicio + " HASTA " + fin;
		}
		else if(formulario.equals("PENDIENTESCDM")) {
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, cdm c, instancia i, usuarios u \r\n" + 
					"WHERE sc.n_id_cdm=ruc.n_id_cdm AND sc.n_id_formulario=f.n_id_formulario AND c.n_id_cdm = sc.n_id_cdm AND ruc.c_usuario = ? \r\n" + 
					"AND u.c_usuario = sc.c_usuario AND i.c_instancia = sc.c_instancia AND DATE(sc.f_fecha_atendido) BETWEEN ? AND ?\r\n" + 
					"ORDER BY sc.f_fecha_atendido ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), inicio, fin);
			tipo = "RESULTADOS DESDE " + inicio + " HASTA " + fin;
		}
		else {
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, resolucion r, cdm c, instancia i, formulario f, usuarios u, movimientos m \r\n" + 
					"WHERE r.id_der_cdm = sc.n_id AND c.n_id_cdm = sc.n_id_cdm AND i.c_instancia = sc.c_instancia\r\n" + 
					"AND f.n_id_formulario = sc.n_id_formulario AND u.c_usuario = r.c_usuario AND m.id_solicitud = sc.n_id \r\n" + 
					"AND r.c_usuario = ? AND DATE(m.f_fecha_movimiento) BETWEEN ? AND ? AND m.s_tipo = 'RESOLUCION' \r\n" + 
					"ORDER BY sc.f_fecha_atendido ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), inicio, fin);
			tipo = "RESULTADOS DESDE " + inicio + " HASTA " + fin;
		}
		model.addAttribute("tipo", tipo);
		model.addAttribute("solicitudes", lista);
		model.addAttribute("formulario", formulario);
		return "vistas/listaObjetos";  
	}
	
	@RequestMapping(value = "/buscarSolicitud/{formulario}/{nombres}/{inicio}/{fin}", method = RequestMethod.GET)
	public String buscarSolicitud(Model model, @RequestParam(value="formulario") String formulario, @RequestParam(value="nombres") String nombres, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, Principal principal) {
		String sql;
		String tipo;
		List<?> lista;
		if(formulario.equals("PENDIENTES")) {
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, instancia i, usuarios u\r\n" + 
					"WHERE sc.n_id_cdm=ruc.n_id_cdm AND sc.n_id_formulario=f.n_id_formulario AND i.c_instancia = sc.c_instancia AND u.c_usuario = sc.c_usuario\r\n" + 
					"AND ruc.c_usuario = ? AND sc.c_usuario = ? AND ((sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) AND (DATE(sc.f_fecha_atendido) BETWEEN ? AND ?))\r\n" + 
					"ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%", inicio, fin);
			tipo = "MIS TAREAS PENDIENTES DE ATENCIÓN";
		}
		else if(formulario.equals("PENDIENTESCDM")){
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, r_usuario_cdm ruc ,formulario f, instancia i, usuarios u WHERE sc.n_id_cdm=ruc.n_id_cdm AND i.c_instancia = sc.c_instancia AND sc.n_id_formulario=f.n_id_formulario\r\n" + 
					"AND u.c_usuario = sc.c_usuario AND ruc.c_usuario = ? AND ((sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) AND (DATE(sc.f_fecha_atendido) BETWEEN ? AND ?))\r\n" + 
					"ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%", inicio, fin);
			tipo = "RESULTADOS";
		}
		else {
			sql = "SELECT sc.n_id, concat(sc.s_nombres, ' ', sc.s_apellidos) as nombresCompletos, c.s_cdm, sc.n_expediente, i.x_nom_instancia, f.form,\r\n" + 
					"date_format(sc.f_registro, '%d-%m-%Y %r') as fecha, sc.s_correo_electronico, sc.n_id_formulario, sc.n_id_cdm, sc.s_estado, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombreAsistente\r\n" + 
					"FROM solicitudes_cdm sc, resolucion r, cdm c, instancia i, formulario f, usuarios u, movimientos m\r\n" + 
					"WHERE r.id_der_cdm = sc.n_id AND c.n_id_cdm = sc.n_id_cdm AND i.c_instancia = sc.c_instancia\r\n" + 
					"AND f.n_id_formulario = sc.n_id_formulario AND u.c_usuario = r.c_usuario AND m.id_solicitud = sc.n_id\r\n" + 
					"AND r.c_usuario = ? AND ((sc.s_nombres LIKE ? OR sc.s_apellidos LIKE ? OR sc.n_expediente LIKE ?) AND (DATE(m.f_fecha_movimiento) BETWEEN ? AND ? AND m.s_tipo = 'RESOLUCION'))\r\n" + 
					"ORDER BY sc.f_registro ASC";
			lista = this.jdbcTemplate.queryForList(sql, principal.getName(), "%"+nombres+"%", "%"+nombres+"%", "%"+nombres+"%", inicio, fin);
			tipo = "RESULTADOS";
		}
		model.addAttribute("tipo", tipo);
		model.addAttribute("solicitudes", lista);
		model.addAttribute("formulario", formulario);
		return "vistas/listaObjetos";  
	}
	
	@GetMapping("/verAgenda") 
	public String verAgenda(Model model, Principal principal) {
		return "vistas/verAgenda";  
	}
	
	@GetMapping("/reportePersonalCDM") 
	public String reporte(Model model, Principal principal) {
		int tipoSolicitud = 1;
		String sql= "call `obtReportePersonalCDM`(?)";
		List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
		model.addAttribute("tipo", "REPORTE");
		model.addAttribute("solicitudes", lista);
		return "vistas/reportePersonalCDM";  
	}
	
	@RequestMapping(value = "/filtrarReportePersonalCDM", method = RequestMethod.GET)
	public String filtrarReporte(Model model, @RequestParam(value="filtro") String filtro, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, @RequestParam(value="tipo") String tipo, Principal principal) {

		String sql, filtroFechas = "", filtroFechasAtendidos = "", filtroTipo="";
		String estado = "'A'";
		if(filtro.equals("1")) 
			filtroFechas = "AND ((DATE(s.f_agenda_1) BETWEEN '" + inicio + "' AND '"+ fin + "') OR (DATE(s.f_agenda_2) BETWEEN '" + inicio + "' AND '"+ fin + "'))";
		if(filtro.equals("3")) 
			filtroFechasAtendidos = "AND (DATE(s.f_fecha_atendido) BETWEEN '" + inicio + "' AND '"+ fin + "')";
		else if(filtro.equals("2")) {
			filtroFechas = "AND (DATE(s.f_registro) BETWEEN '" + inicio + "' AND '"+ fin + "')";
			estado = "'E','D','R','X'";
		}
		else if(filtro.equals("0"))
			estado = "'E','D','R','X'";
		
		if(!tipo.equals("0"))
			filtroTipo = "AND s.n_id_formulario = " + tipo;
			
		sql = "SELECT CONCAT(s.s_nombres, ' ', s.s_apellidos) AS nombresCompletos, DATE_FORMAT(s.f_registro, '%d-%m-%Y %r')AS fechaSolicitud, DATE_FORMAT(s.f_fecha_atendido, '%d-%m-%Y %r')AS fechaAtendido, s.n_expediente, s.s_celular, i.x_nom_instancia, f.form, \r\n" + 
				"		CONCAT(COALESCE(s.s_motivo,''), ' ', COALESCE(s.s_tiempo,''), ' ', COALESCE(s.s_tipo_copias,''), ' ', COALESCE(s.s_cantidad_folios,''), ' ', COALESCE(s.s_numero_fojas,''), ' ', COALESCE(s.s_actuados,'')) AS detalle,\r\n" + 
				"		DATE_FORMAT(s.f_agenda_1, '%d-%m-%Y %r') AS agenda1, date_format(s.f_agenda_2, '%d-%m-%Y %r') AS agenda2\r\n" + 
				"		from solicitudes_cdm s\r\n" + 
				"		INNER JOIN instancia i ON i.c_instancia = s.c_instancia INNER JOIN formulario f ON f.n_id_formulario = s.n_id_formulario\r\n" + 
				"		WHERE s.c_usuario = ? " + filtroTipo + " AND s.s_estado IN (" + estado + ") "+ filtroFechas + " " + filtroFechasAtendidos + " ORDER BY s.f_fecha_atendido ASC;";
		List<?>	lista = this.jdbcTemplate.queryForList(sql, principal.getName());

		model.addAttribute("tipo", tipo);
		model.addAttribute("solicitudes", lista);

		return "vistas/reportePersonalCDM";  
	}
	
	@GetMapping("/500")
	public String error() {
		return "500";
	}
	
	@GetMapping("/reporteGeneralCDM") 
	public String reporteGeneral(Model model, Principal principal) {
		
		return "vistas/reporteGeneralCDM";  
	}
	
	@GetMapping("/reporteDetalladoCDM") 
	public String reporteDetallado(Model model, Principal principal) {
		
		return "vistas/reporteDetalladoCDM";  
	}
	
	@GetMapping("/MESAPARTESS")
	public String mpeemergencia(Model model, String sede, Principal principal) {
		try {
			String sql = "select idperfil from usuarios where c_usuario = ?";
			int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
			List<?> lista = null;
			if(perfil == 5) {
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" + 
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.c_usuario_asignado = ? and c.s_estado = 'P' and s_superior = 'N' ORDER BY c.f_sistema asc";
				lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			}
			else if (perfil == 15) {
				if(sede == null) 
					sede = "0401";
				
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" +
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.s_estado = 'P' and c.c_sede = ? ORDER BY c.f_sistema asc";
				lista = this.jdbcTemplate.queryForList(sql, sede);
			}
			sql = "select c_sede, s_sede from sede";
			List<?> sedes = this.jdbcTemplate.queryForList(sql);
			model.addAttribute("sedes", sedes);
			model.addAttribute("documentos", lista);
			model.addAttribute("tipo", "MPE - DOCUMENTOS PENDIENTES");
			model.addAttribute("formulario", "MESAPARTES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaDocumentos";
	}
	
	@GetMapping("/URGENTESS")
	public String urgentes(Model model, Principal principal) {
		try {

			String sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
				"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
				"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" +
				"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.s_estado = 'P' and c.s_superior = 'S' ORDER BY c.f_sistema asc";
			List<?> lista = this.jdbcTemplate.queryForList(sql);
			
			model.addAttribute("documentos", lista);
			model.addAttribute("tipo", "MPE - URGENTES");
			model.addAttribute("formulario", "URGENTES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaUrgentes";
	}
	
	
	
	@RequestMapping(value = "/buscarDocumentoCDG", method = RequestMethod.GET)
	public String buscarDocumentoFecha(Model model, @RequestParam(value="tipo") String tipo, @RequestParam(value="texto") String texto, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, Principal principal) {
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		List<?> lista = null;
		
		if(tipo.equals("T")) {
			if(perfil == 5) {
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" + 
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.c_usuario_asignado = ? and (u.s_nombres like ? or u.s_apellidos like ? or c.x_expediente like ?)";
				lista = this.jdbcTemplate.queryForList(sql, principal.getName(), "%" + texto + "%", "%" + texto + "%", "%" + texto + "%");
			}
			else if (perfil == 15) {
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" +
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where (u.s_nombres like ? or u.s_apellidos like ? or c.x_expediente like ?)";
				lista = this.jdbcTemplate.queryForList(sql, "%" + texto + "%", "%" + texto + "%", "%" + texto + "%");
			}
		}
		else if(tipo.equals("F")) {
			if(perfil == 5) {
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" + 
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.c_usuario_asignado = ? and date(c.f_sistema) >= ? AND date(c.f_sistema) <= ?";
				lista = this.jdbcTemplate.queryForList(sql, principal.getName(), inicio, fin);
			}
			else if (perfil == 15) {
				sql = "SELECT c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaIngreso, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado, c.s_superior from cdg c \r\n" + 
						"INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia\r\n" + 
						"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado\r\n" +
						"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where date(c.f_sistema) >= ? AND date(c.f_sistema) <= ?";
				lista = this.jdbcTemplate.queryForList(sql, inicio, fin);
			}
		}
		
		model.addAttribute("documentos", lista);
		model.addAttribute("tipo", "BUSQUEDA " + inicio + " - " + fin);
		model.addAttribute("formulario", "MESAPARTES");
		return "vistas/listaDocumentos";  
	}
	

	
	@GetMapping("/programacion") 
	public String programacion(Model model, Principal principal) {
		try {
			List<?> lista = null;
			String sql = "select idperfil from usuarios where c_usuario = ?";
			int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
			if(perfil == 15) {
				sql = "select c_sede as id_sede, s_sede as nombre_sede from sede";
				lista = this.jdbcTemplate.queryForList(sql);
			}
			else if(perfil == 7) {
				sql = "SELECT so.c_sede as id_sede, s.s_sede as nombre_sede FROM sedeorganousuario so INNER JOIN sede s ON s.c_sede = so.c_sede\r\n" + 
						"WHERE so.c_usuario = ? GROUP BY so.c_sede";
				lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			}
			model.addAttribute("sedes", lista);
		}
		catch(Exception ex) {
			
		}
		return "vistas/programacion";
	}
	
	@GetMapping("/reporteCDG") 
	public String reporteCDG(Model model, Principal principal) {
		try {
			String sql = "SELECT so.n_id_sou, concat(s.s_sede, ' - ',o.s_organo) as oojj FROM sedeorganousuario so INNER JOIN sede s ON s.c_sede = so.c_sede\r\n" + 
					"INNER JOIN organos o ON o.n_id_organo = so.n_id_organo WHERE so.c_usuario = ? AND so.n_activo = 1";
			List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
			model.addAttribute("organos", lista);
		}
		catch(Exception ex) {
			
		}
		return "vistas/reporteCDG";
	}
	
	@GetMapping("/reporteSIJ") 
	public String reporteSIJ(Model model, Principal principal) {
		try {
			String sql = "select s_dni from usuarios where c_usuario = ?";
			String dni = this.jdbcTemplate.queryForObject(sql, String.class, "JCANALES");
			sql = "SELECT DISTINCT s.c_sede, s.x_desc_sede, u.c_usuario FROM usuario u ,sede s, usuario_instancia ui WHERE u.c_perfil='06' AND u.l_activo='S' AND u.c_dni = ? AND\r\n" + 
				"u.c_usuario=ui.c_usuario AND ui.l_activo='S' AND ui.c_sede=s.c_sede ORDER BY s.x_desc_sede";
			List<?> lista = this.jdbcTemplate2.queryForList(sql, dni);
			model.addAttribute("sedes", lista);
		}
		catch(Exception ex) {
			
		}
		return "vistas/reporteSIJ";
	}
}
