package com.cdm.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.config.Conexion;
import com.cdm.entities.Calculo;
import com.cdm.entities.Turno2;

@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@RestController
public class WebServiceDP {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    } 
    
	@RequestMapping(value="/wsRestSaveDP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> guardarTurno(@RequestBody Turno2 turno) {
		Map<String,Object> map = new HashMap<String,Object>();
		String ip = getClientIp();
		try {
			if(!ip.equals("172.28.3.11")) map.put("Status", 400); 
			else {
				String sql = "";
				if(turno.getTipo().equals("D")){
					String[] fechas = turno.getFechas().split(",");
					int cantidad = fechas.length;
					for(int i = 0; i < cantidad; i++) {
						sql = "insert into dp_programacion (n_id_defensor, n_id_ubicacion, f_turno, c_usuario, f_sistema) values (?,?,?,?,NOW())";
						this.jdbcTemplate.update(sql, turno.getDefensor(), turno.getSede(), fechas[i], turno.getUsuario());
					}
				}
				else if(turno.getTipo().equals("M")){
					sql = "SELECT CAST(DAY(NOW()) AS CHAR) AS inicio, CAST(DAY(LAST_DAY(?)) AS CHAR) AS fin, IF(MONTH(?) = MONTH(NOW()) , 'R', 'C') AS operacion";
					List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, turno.getFechas() + "/01", turno.getFechas() + "/01");
			        Calculo calculo = new Calculo();
			        for (Map row : rows) {
			        	calculo.setFecha((String) row.get("inicio"));
			        	calculo.setFecha2((String) row.get("fin"));
			        	calculo.setOperacion((String) row.get("operacion"));
			        }
			        
			        int i = 1;
			        if(calculo.getOperacion().equals("R"))
			        	i = Integer.valueOf(calculo.getFecha());
			        
			        int numDias = Integer.valueOf(calculo.getFecha2());
					
			        while (i <= numDias) {  
						String cadenaFecha = "";
						cadenaFecha = turno.getFechas() + "/"+ i;
						sql = "insert into dp_programacion (n_id_ubicacion, n_id_defensor, f_turno, c_usuario, f_sistema) values (?,?,?,?,NOW())";
						this.jdbcTemplate.update(sql, turno.getSede(), turno.getDefensor(), cadenaFecha, turno.getUsuario());
						i++;
					}
				}
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			map.put("Status", 500);
		}
		return map;
	}
}
