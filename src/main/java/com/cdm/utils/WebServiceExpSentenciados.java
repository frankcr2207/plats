package com.cdm.utils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.config.Conexion;
import com.cdm.entities.Expediente;
import com.cdm.entities.Proceso;
import com.cdm.entities.Regla;
import com.cdm.entities.Secretario;
import com.cdm.entities.ValueCriteria;

@RestController
@CrossOrigin
public class WebServiceExpSentenciados {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());
	
	@RequestMapping(value = "/obtExpedientesSentenciados", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Expediente> obtExpedientesSentenciados(@RequestBody ValueCriteria vc) {
    	String sql = "select r.id_expediente, r.n_expediente, s.s_sede, i.x_nom_instancia from rc_expediente r, instancia i, sede s \r\n" + 
    			"where i.c_instancia = r.c_instancia and s.c_sede = r.c_sede and r.id_sentenciado = ?";
    	List<Expediente> expedientes = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        
        int val = 0;
        for (@SuppressWarnings("rawtypes") Map row : rows) {
            Expediente exp = new Expediente();
            val = (int) row.get("id_expediente");
            exp.setId_correlativo((String) Integer.toString(val));
            exp.setNumero((String) row.get("n_expediente"));
            exp.setId_instancia((String) row.get("s_sede"));
            exp.setInstancia((String) row.get("x_nom_instancia"));
            expedientes.add(exp);
        }
        
    	return expedientes;
    }
	
	@RequestMapping(value = "/obtDatosExpedienteSentenciado", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Proceso obtDatosExpedienteSentenciados(@RequestBody ValueCriteria vc) {
    	String sql = "select rc.id_expediente, rc.n_expediente, s.c_sede, s.s_sede, i.c_instancia, i.x_nom_instancia, sec.id_secretario, CONCAT(sec.s_nombres, ' ', sec.s_apellido_paterno, ' ',sec.s_apellido_materno),\r\n" + 
    			"COALESCE(rc.n_reparacion, 0) reparacion, COALESCE((SELECT SUM(r.n_reparacion) FROM rc_reglas r WHERE r.id_expediente = ?), 0) AS acumulado, COALESCE(rc.s_medidas,'') as medidas, rc.s_estado \r\n" + 
    			"from rc_expediente rc, instancia i, sede s, rc_secretario sec where i.c_instancia = rc.c_instancia and s.c_sede = rc.c_sede and\r\n" + 
    			"rc.id_secretario = sec.id_secretario and rc.id_expediente = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId(), vc.getId());
        Proceso proceso = new Proceso();
        for (@SuppressWarnings("rawtypes") Map row : rows) {
        	proceso.setId_expediente((int) row.get("id_expediente"));
        	proceso.setExpediente((String) row.get("n_expediente"));
        	proceso.setId_sede((String) row.get("c_sede"));
        	proceso.setSede((String) row.get("s_sede"));
        	proceso.setId_instancia((String) row.get("c_instancia"));
        	proceso.setInstancia((String) row.get("x_nom_instancia"));
        	proceso.setId_secretario((int) row.get("id_secretario"));
        	proceso.setSecretario((String) row.get("nomSec"));
        	proceso.setReparacion((float) row.get("reparacion"));
        	proceso.setAcumulado((double) row.get("acumulado"));
        	proceso.setMedidas((String) row.get("medidas"));
        	proceso.setEstado((String) row.get("s_estado"));
        }
        
    	return proceso;
    }
	
	@RequestMapping(value = "/obtReglasAnteriores", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Regla> obtReglasAnteriores(@RequestBody ValueCriteria vc) {
    	String sql = "SELECT DATE_FORMAT(r.f_cita, '%d-%m-%Y %r') as registro, COALESCE(DATE_FORMAT(r.f_regla, '%d-%m-%Y %r'), '') as regla, COALESCE(r.s_ruta_archivo,'') as ruta, COALESCE(r.s_nombre_archivo, '') as archivo, COALESCE(r.s_observaciones, '') as observacion, r.c_usuario, r.n_reparacion, r.s_estado, r.n_id FROM rc_reglas r WHERE r.id_expediente = ? ORDER BY r.f_cita desc";
    	List<Regla> reglas = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        
        for (@SuppressWarnings("rawtypes") Map row : rows) {
        	Regla regla = new Regla();
        	regla.setId((int) row.get("n_id"));
            regla.setRegistro((String) row.get("registro"));
            regla.setRegla((String) row.get("regla"));
            regla.setRuta((String) row.get("ruta"));
            regla.setArchivo((String) row.get("archivo"));
            regla.setObservaciones((String) row.get("observacion"));
            regla.setReparacion((float) row.get("n_reparacion"));
            regla.setEstado((String) row.get("s_estado"));
            regla.setUsuario((String) row.get("c_usuario"));
            reglas.add(regla);
        }
        
    	return reglas;
    }
	
	@RequestMapping(value = "/obtPendientes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody int obtPendientes(@RequestBody ValueCriteria vc, Principal principal) {
    	String sql = "SELECT COUNT(*) FROM rc_sentenciados r WHERE r.c_usuario_temp = ?";
        int cantidad = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
        ValueCriteria vc2 = new ValueCriteria();
        vc2.setId(Integer.toString(cantidad));
    	return cantidad;
    }
	
	@RequestMapping(value = "/obtSecretariosNCPP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Secretario> obtSecretariosNCPP(@RequestBody ValueCriteria vc) {
    	String sql = "SELECT u.id_secretario, CONCAT(u.s_apellido_paterno, ' ', u.s_apellido_materno, ', ',u.s_nombres) as nombresCompletos \r\n" + 
    			"FROM rc_secretario u WHERE u.s_estado = 'S' ORDER BY u.s_apellido_paterno ASC";
    	List<Secretario> secretarios = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        
        for (Map row : rows) {
            Secretario sec = new Secretario();
            sec.setC_usuario((String) Integer.toString((int) row.get("id_secretario")));
            sec.setNombresCompletos((String) row.get("nombresCompletos"));
            secretarios.add(sec);
        }
        
    	return secretarios;
    }
}
