package com.cdm.utils;

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
import com.cdm.entities.Cdm;
import com.cdm.entities.Devolucion;
import com.cdm.entities.DevolucionCriteria;
import com.cdm.entities.Instancia;
import com.cdm.entities.Secretario;
import com.cdm.entities.ValueCriteria;

@RestController
@CrossOrigin
public class WebServiceSecretarios {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());
	
	@RequestMapping(value = "/obtAsistentes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Secretario> obtAsistentes() {
    	String sql = "SELECT u.c_usuario, CONCAT(u.c_usuario, ' - ', u.s_nombres, ' ',u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombresCompletos FROM usuarios u WHERE u.idperfil = 5 AND u.s_activo = 'S' ORDER BY u.s_nombres ASC ";
    	List<Secretario> secretarios = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        
        for (Map row : rows) {
            Secretario sec = new Secretario();
            sec.setC_usuario((String) row.get("c_usuario"));
            sec.setNombresCompletos((String) row.get("nombresCompletos"));
            secretarios.add(sec);
        }
        
    	return secretarios;
    }
	
	@RequestMapping(value = "/obtTodoSecretarios", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Secretario> obtTodoSecretarios() {
    	String sql = "SELECT u.c_usuario, CONCAT(u.c_usuario, ' - ', u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombresCompletos \r\n" + 
    			"FROM usuarios u WHERE u.s_activo = 'S' AND u.idperfil = 10 order by u.s_nombres asc";
    	List<Secretario> secretarios = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        
        for (Map row : rows) {
            Secretario sec = new Secretario();
            sec.setC_usuario((String) row.get("c_usuario"));
            sec.setNombresCompletos((String) row.get("nombresCompletos"));
            secretarios.add(sec);
        }
        
    	return secretarios;
    }
	
	@RequestMapping(value = "/obtSecretarios", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Secretario> obtSecretarios(@RequestBody ValueCriteria vc) {
    	String sql = "SELECT u.c_usuario, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombresCompletos \r\n" + 
    			"FROM r_usuario_instancia r INNER JOIN usuarios u ON u.c_usuario = r.c_usuario WHERE r.c_instancia = ? AND u.s_activo = 'S' AND u.idperfil = 10";
    	List<Secretario> secretarios = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        
        for (Map row : rows) {
            Secretario sec = new Secretario();
            sec.setC_usuario((String) row.get("c_usuario"));
            sec.setNombresCompletos((String) row.get("nombresCompletos"));
            secretarios.add(sec);
        }
        
    	return secretarios;
    }
	
	@RequestMapping(value = "/verMotivoDevolucion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<DevolucionCriteria> verMotivoDevolucion(@RequestBody ValueCriteria vc) {
    	String sql = "SELECT CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) AS nombresCompletos, date_format(m.f_fecha_movimiento, '%d-%m-%Y %r') as fechaMovimiento, m.s_observacion \r\n" + 
    			"FROM movimientos m INNER JOIN usuarios u ON u.c_usuario = m.c_usuario_origen\r\n" + 
    			"WHERE m.id_solicitud = ? AND m.s_tipo = 'DEVOLUCION' ORDER BY m.f_fecha_movimiento DESC LIMIT 1";
    	List<DevolucionCriteria> valores = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        
        for (Map row : rows) {
        	DevolucionCriteria dev = new DevolucionCriteria();
        	dev.setSecretario((String) row.get("nombresCompletos"));
        	dev.setFecha((String) row.get("fechaMovimiento"));
        	dev.setObservacion((String) row.get("s_observacion"));
            valores.add(dev);
        }
        
    	return valores;
    }

}
