package com.cdm.utils;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.config.Conexion;
import com.cdm.entities.Cdm;
import com.cdm.entities.Parametro;
import com.cdm.entities.Resultado;
import com.cdm.entities.Secretario;
import com.cdm.entities.Solicitud;
import com.cdm.entities.Usuario;
import com.cdm.entities.ValueCriteria;


@RestController
@CrossOrigin
public class WebServiceAdmCDM {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());
	
	@RequestMapping(value = "/obtCDMAdm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtCDMAdm(Principal principal) {
    	String sql = "SELECT * FROM cdm c INNER JOIN r_usuario_cdm r ON r.n_id_cdm = c.n_id_cdm WHERE r.c_usuario = ? AND c.s_activo = 'S'";
    	List<Cdm> cdms = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, principal.getName());
        
        for (Map row : rows) {
            Cdm cdm = new Cdm();
            cdm.setId((int) row.get("n_id_cdm"));
            cdm.setNombres((String) row.get("s_cdm"));
            cdms.add(cdm);
        }
    	return cdms;
    }
	
	@RequestMapping(value = "/obtOrganos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtOrganos(Principal principal, @RequestBody ValueCriteria vc) {
    	String sql = "SELECT o.n_id_organo, o.s_organo FROM sede_organo so INNER JOIN organos o ON o.n_id_organo = so.n_id_organo\r\n" + 
    			"WHERE so.c_sede = ?";
    	List<Cdm> cdms = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        for (Map row : rows) {
            Cdm cdm = new Cdm();
            cdm.setId((int) row.get("n_id_organo"));
            cdm.setNombres((String) row.get("s_organo"));
            cdms.add(cdm);
        }
    	return cdms;
    }
	

	@RequestMapping(value = "/obtReporteAdmCDM", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Solicitud> obtReporteCDMAdm(@RequestBody Resultado res, Principal principal) {
		String cod = "", query = "";
		List<Map<String, Object>> rows = null;
		if(res.getId().equals("T")) {
			query = "SELECT r.n_id_cdm FROM r_usuario_cdm r WHERE r.c_usuario = ?";
			List<Integer> lista = this.jdbcTemplate.query(query, new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int rowNum) 
	            	throws SQLException {
	            		return rs.getInt(1);
							}
								}, principal.getName());
			for(int i = 0; i < lista.size(); i++)
				cod = cod + lista.get(i) + ",";
			
			cod = cod.substring(0, cod.length() - 1);
			query = "SELECT DISTINCT (select cdm.s_cdm FROM cdm WHERE cdm.n_id_cdm =solicitudes_cdm.n_id_cdm) AS des_cdm, \r\n" + 
					"solicitudes_cdm.n_id_cdm, solicitudes_cdm.c_usuario, CONCAT(usuarios.s_nombres, ' ', usuarios.s_apellido_paterno, ' ', usuarios.s_apellido_materno) AS nombresCompletos,\r\n" + 
					"funcContarSolicitudCDM(solicitudes_cdm.c_usuario,n_id_cdm,'P', ?, ?) AS PENDIENTES,\r\n" + 
					"funcContarSolicitudCDM(solicitudes_cdm.c_usuario,n_id_cdm,'A', ?, ?) AS ATENDIDOS  \r\n" + 
					"FROM solicitudes_cdm, usuarios\r\n" + 
					"WHERE s_estado IN ('E','D','R','X','P','A') AND usuarios.c_usuario = solicitudes_cdm.c_usuario\r\n" + 
					"AND (DATE(f_registro) BETWEEN ? AND ?) AND  n_id_cdm IN (" + cod + ") ORDER BY des_cdm";
			rows = jdbcTemplate.queryForList(query, res.getFecha1(), res.getFecha2(), res.getFecha1(), res.getFecha2(), res.getFecha1(), res.getFecha2());
		}else {
			query = "SELECT DISTINCT (select cdm.s_cdm FROM cdm WHERE cdm.n_id_cdm =solicitudes_cdm.n_id_cdm) AS des_cdm, \r\n" + 
					"solicitudes_cdm.n_id_cdm, solicitudes_cdm.c_usuario, CONCAT(usuarios.s_nombres, ' ', usuarios.s_apellido_paterno, ' ', usuarios.s_apellido_materno) AS nombresCompletos,\r\n" + 
					"funcContarSolicitudCDM(solicitudes_cdm.c_usuario,n_id_cdm,'P', ?, ?) AS PENDIENTES,\r\n" + 
					"funcContarSolicitudCDM(solicitudes_cdm.c_usuario,n_id_cdm,'A', ?, ?) AS ATENDIDOS  \r\n" + 
					"FROM solicitudes_cdm, usuarios\r\n" + 
					"WHERE s_estado IN ('E','D','R','X','P','A') AND usuarios.c_usuario = solicitudes_cdm.c_usuario\r\n" + 
					"AND (DATE(f_registro) BETWEEN ? AND ?) AND  n_id_cdm IN (?) ORDER BY des_cdm";
			rows = jdbcTemplate.queryForList(query, res.getFecha1(), res.getFecha2(), res.getFecha1(), res.getFecha2(), res.getFecha1(), res.getFecha2(), res.getId());
		}
		
    	List<Solicitud> solicitudes = new ArrayList<>();

    	int val = 0, val2 = 0;
        for (Map row : rows) {
            Solicitud solicitud = new Solicitud();
            solicitud.setCdm((String) row.get("des_cdm"));
            solicitud.setNombres((String) row.get("nombresCompletos"));
            val = (int) row.get("PENDIENTES");
            solicitud.setCelular((String) Integer.toString(val));
            val2 = (int) row.get("ATENDIDOS");
            solicitud.setCorreo((String) Integer.toString(val2));
            solicitudes.add(solicitud);
        }
        return solicitudes;
    }
	
	@RequestMapping(value = "/obtReporteDetAdmCDM", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Solicitud> obtReporteDetCDMAdm(@RequestBody Resultado res, Principal principal) {
		String cod = "", query = "";
		List<Map<String, Object>> rows = null;
		if(res.getId().equals("T")) {
			query = "SELECT r.n_id_cdm FROM r_usuario_cdm r WHERE r.c_usuario = ?";
			List<Integer> lista = this.jdbcTemplate.query(query, new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int rowNum) 
	            	throws SQLException {
	            		return rs.getInt(1);
							}
								}, principal.getName());
			for(int i = 0; i < lista.size(); i++)
				cod = cod + lista.get(i) + ",";
			
			cod = cod.substring(0, cod.length() - 1);
			query = "SELECT c.s_cdm,f.s_formulario,s.n_expediente,i.x_nom_instancia,date_format(s.f_registro, '%d-%m-%Y %r') as fechaRegistro, COALESCE(s.c_usuario,'SIN RESPONSABLE') as usuario, s.s_estado,case s.s_estado\r\n" + 
					"        when 'N' then 'NUEVO' when 'D' then 'PENDIENTE'\r\n" + 
					"        when 'R' then 'PENDIENTE'  when 'X' then 'PENDIENTE'\r\n" + 
					"        when 'E' then 'PENDIENTE'  end as tipoEstado\r\n" + 
					"FROM solicitudes_cdm s , cdm c, formulario f, instancia i  \r\n" + 
					"WHERE s.n_id_cdm=c.n_id_cdm AND s.n_id_formulario=f.n_id_formulario AND s.c_instancia=i.c_instancia AND\r\n" + 
					"s.s_estado IN ('E','D','R','X','N') AND (DATE(s.f_registro) BETWEEN ? AND ?) AND  s.n_id_cdm IN (" + cod + ") \r\n" + 
					"ORDER BY tipoEstado,c.s_cdm,f.s_formulario;";
			rows = jdbcTemplate.queryForList(query, res.getFecha1(), res.getFecha2());
		}
		else {
			query = "SELECT c.s_cdm,f.s_formulario,s.n_expediente,i.x_nom_instancia,date_format(s.f_registro, '%d-%m-%Y %r') as fechaRegistro, COALESCE(s.c_usuario,'SIN RESPONSABLE') as usuario, s.s_estado,case s.s_estado\r\n" + 
					"        when 'N' then 'NUEVO' when 'D' then 'PENDIENTE'\r\n" + 
					"        when 'R' then 'PENDIENTE' when 'X' then 'PENDIENTE'\r\n" + 
					"        when 'E' then 'PENDIENTE' end as tipoEstado\r\n" + 
					"FROM solicitudes_cdm s , cdm c, formulario f, instancia i  \r\n" + 
					"WHERE s.n_id_cdm=c.n_id_cdm AND s.n_id_formulario=f.n_id_formulario AND s.c_instancia=i.c_instancia AND\r\n" + 
					"s.s_estado IN ('E','D','R','X','N') AND (DATE(s.f_registro) BETWEEN ? AND ?) AND  s.n_id_cdm IN (?) \r\n" + 
					"ORDER BY tipoEstado,c.s_cdm,f.s_formulario;";
			rows = jdbcTemplate.queryForList(query, res.getFecha1(), res.getFecha2(), res.getId());
		}
		
    	List<Solicitud> solicitudes = new ArrayList<>();

        for (Map row : rows) {
            Solicitud solicitud = new Solicitud();
            solicitud.setCdm((String) row.get("s_cdm"));
            solicitud.setExpediente((String) row.get("n_expediente"));
            solicitud.setInstancia((String) row.get("x_nom_instancia"));
            solicitud.setDocumento((String) row.get("s_formulario"));
            solicitud.setFecha((String) row.get("fechaRegistro"));
            solicitud.setNombres((String) row.get("usuario"));
            solicitud.setEstado((String) row.get("tipoEstado"));
            solicitudes.add(solicitud);
        }
        return solicitudes;
    }
	
	@RequestMapping(value = "/obtenerCDMAsignados", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtenerCDMAsignados(Principal principal, @RequestBody ValueCriteria vc) {
		String sql = "SELECT c.n_id_cdm, c.s_cdm from cdm c INNER JOIN r_usuario_cdm r ON r.n_id_cdm = c.n_id_cdm\r\n" + 
				"INNER JOIN usuarios u ON u.c_usuario = r.c_usuario WHERE u.c_usuario = ?";
    	List<Cdm> cdms = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        for (Map row : rows) {
            Cdm cdm = new Cdm();
            cdm.setId((int) row.get("n_id_cdm"));
            cdm.setNombres((String) row.get("s_cdm"));
            cdms.add(cdm);
        }
    	return cdms;
    }
	
	@RequestMapping(value = "/obtenerCDGAsignados", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtenerCDGAsignados(Principal principal, @RequestBody ValueCriteria vc) {
		String sql = "SELECT so.n_id_sou, concat(s.s_sede, ' - ',o.s_organo) as oojj FROM sedeorganousuario so INNER JOIN sede s ON s.c_sede = so.c_sede\r\n" + 
				"INNER JOIN organos o ON o.n_id_organo = so.n_id_organo WHERE so.c_usuario = ? AND so.n_activo = 1";
    	List<Cdm> cdms = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        for (Map row : rows) {
            Cdm cdm = new Cdm();
            cdm.setId((int) row.get("n_id_sou"));
            cdm.setNombres((String) row.get("oojj"));
            cdms.add(cdm);
        }
    	return cdms;
    }
	
	@RequestMapping(value = "/obtenerUsuariosSede", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Usuario> obtenerUsuariosSede(Principal principal, @RequestBody ValueCriteria vc) {
		String sql = "SELECT s.c_usuario, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) AS nombresCompletos FROM sedeorganousuario s \r\n" + 
			"INNER JOIN usuarios u ON u.c_usuario = s.c_usuario WHERE s.c_sede = ? AND u.s_activo = 'S' AND u.idperfil = 5 GROUP BY s.c_usuario";
    	List<Usuario> usuarios = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        for (Map row : rows) {
            Usuario usuario = new Usuario();
            usuario.setUser((String) row.get("c_usuario"));
            usuario.setNombres((String) row.get("nombresCompletos"));
            usuarios.add(usuario);
        }
    	return usuarios;
    }
	
	@RequestMapping(value = "/obtReporteCDG", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Solicitud> obtReporteCDG(@RequestBody ValueCriteria vc, Principal principal) {

		String query = "SELECT t.s_tipo_documento,c.x_expediente,s.s_sede,o.s_organo, i.x_nom_instancia,c.c_usuario_asignado, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaRegistro, CONCAT(DATEDIFF(CURDATE(), c.f_sistema)) AS retraso FROM cdg c , sede s , organos o, instancia i ,tipo_documento t\r\n" + 
				"WHERE c.c_sede=s.c_sede AND c.n_id_organo=o.n_id_organo AND c.c_instancia=i.c_instancia AND c.s_estado='P'\r\n" + 
				"AND c.n_id_tipo_documento=t.n_id_tipo_documento \r\n" + 
				"AND c.n_id_organo = (SELECT n_id_organo FROM sedeorganousuario WHERE n_id_sou = ? AND c_usuario = ?)\r\n" + 
				"AND c.c_sede = (SELECT c_sede FROM sedeorganousuario WHERE n_id_sou = ? AND c_usuario = ?)\r\n" + 
				"ORDER BY c.f_sistema";
		
		System.out.println(vc.getId());
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, vc.getId(), principal.getName(), vc.getId(), principal.getName());
    	List<Solicitud> solicitudes = new ArrayList<>();
        for (Map row : rows) {
            Solicitud solicitud = new Solicitud();
            solicitud.setExpediente((String) row.get("x_expediente"));
            solicitud.setFecha((String) row.get("fechaRegistro"));
            solicitud.setDocumento((String) row.get("s_tipo_documento"));
            solicitud.setEstado((String) row.get("s_sede"));
            solicitud.setCdm((String) row.get("s_organo"));
            solicitud.setInstancia((String) row.get("x_nom_instancia"));
            solicitud.setNombres((String) row.get("c_usuario_asignado"));
            solicitud.setNacimiento((String) row.get("retraso"));
            solicitudes.add(solicitud);
        }
        return solicitudes;
    }

}
