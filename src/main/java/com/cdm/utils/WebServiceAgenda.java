package com.cdm.utils;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.config.Conexion;
import com.cdm.entities.EventosEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin
public class WebServiceAgenda {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());

    @GetMapping(value = "all")
    public String getEvents(Principal principal) {
        String jsonMsg = null;
        try {
            List<?> events = new ArrayList<EventosEntity>();
            String sql = "SELECT concat(s.c_usuario, ' --> ', s_descripcion) as title, a.f_evento_inicio as start, a.f_evento_fin as end, c_color as color FROM r_usuario_cdm r,solicitudes_cdm s,agenda a\r\n" + 
            "WHERE r.c_usuario = ? AND r.n_id_cdm = s.n_id_cdm AND s.n_id = a.id_solicitud AND a.f_evento_inicio >= DATE_SUB(NOW(), INTERVAL 15 DAY) ORDER BY a.f_evento_inicio";
            events = this.jdbcTemplate.queryForList(sql, principal.getName());
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
    }
    
    @GetMapping(value = "allS")
    public String getEvents2(Principal principal) {
        String jsonMsg = null;
        try {
            List<?> events = new ArrayList<EventosEntity>();
            String sql = "SELECT a.s_descripcion as title, a.f_evento_inicio as start, a.f_evento_fin as end, s_color as color FROM rc_agenda a\r\n" + 
            		"WHERE a.c_usuario = ? ORDER BY a.f_evento_inicio";
            events = this.jdbcTemplate.queryForList(sql, principal.getName());
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
    }
    
    @RequestMapping(value="/obtenerTurnos/{sede}", method=RequestMethod.GET)
    public String getEvents3(@PathVariable("sede") String sede, Principal principal) {
        String jsonMsg = null;
        try {
            List<?> events = new ArrayList<EventosEntity>();
            String sql = "";
            sql = "SELECT id_programacion as id, concat(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as title, c.f_fecha as start, ADDDATE(c.f_fecha, INTERVAL '24' HOUR) as end, concat('#0086E0') as color FROM cdg_programacion c \r\n" + 
            		"INNER JOIN usuarios u on u.c_usuario = c.c_usuario WHERE c.c_sede = ? ORDER BY c.f_fecha asc";
            events = this.jdbcTemplate.queryForList(sql, sede);
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);

        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
    }
    
    @RequestMapping(value="/wsRestGetScheduleDP/{sede}", method=RequestMethod.GET)
    public String wsRestGetScheduleDP(@PathVariable("sede") String sede, Principal principal) {
        String jsonMsg = null;
        try {
            List<?> events = new ArrayList<EventosEntity>();
            String sql = "";
            sql = "SELECT n_id_programacion as id, concat(u.s_nombres, ' ', u.s_apellidos) as title, c.f_turno as start, ADDDATE(c.f_turno, INTERVAL '24' HOUR) as end, concat('#0086E0') as color FROM dp_programacion c \r\n" + 
            	"INNER JOIN dp_defensor u on u.n_id_defensor = c.n_id_defensor WHERE c.n_id_ubicacion = ? ORDER BY c.f_turno asc";
            events = this.jdbcTemplate.queryForList(sql, sede);
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);

        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
    }
}
