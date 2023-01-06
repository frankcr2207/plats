package com.cdm.utils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.config.Conexion;
import com.cdm.entities.Instancia;
import com.cdm.entities.Parametro;
import com.cdm.entities.Reasignacion;
import com.cdm.entities.Usuario;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class WebServiceSIJ {
	
	Conexion con = new Conexion();
	JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
	
	@RequestMapping(value = "/obtenerUsuarioEsp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Parametro> obtenerUsuarioEsp(Principal principal, @RequestBody Parametro param) {
		String sql = "SELECT DISTINCT e.c_especialidad,e.x_desc_especialidad FROM usuario_instancia ui,especialidad_instancia ei,especialidad e\r\n" + 
				" WHERE ui.c_usuario = ? AND ui.l_activo='S' AND ui.c_instancia=ei.c_instancia AND ei.c_especialidad=e.c_especialidad AND ui.c_sede = ?";
    	List<Parametro> parametros = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate2.queryForList(sql, param.getValor(), param.getCodigo());
        for (Map row : rows) {
            Parametro parametro = new Parametro();
            parametro.setCodigo((String) row.get("c_especialidad"));
            parametro.setValor((String) row.get("x_desc_especialidad"));
            parametros.add(parametro);
        }
    	return parametros;
    }
	
	@RequestMapping(value = "/obtenerUsuarioIns", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtenerUsuarioIns(Principal principal, @RequestBody Reasignacion res) {
		String sql = "SELECT i.x_nom_instancia,i.c_instancia FROM usuario_instancia ui,especialidad_instancia ei,instancia i\r\n" + 
			"WHERE ui.c_usuario = ? AND ui.l_activo='S' AND ui.c_instancia=ei.c_instancia \r\n" + 
			"AND ui.c_instancia=i.c_instancia AND ei.c_especialidad= ? AND ui.c_sede= ?";
    	List<Instancia> instancias = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate2.queryForList(sql, res.getValor(), res.getDestino(), res.getOrigen());
        for (Map row : rows) {
            Instancia ins = new Instancia();
            ins.setC_instancia((String) row.get("c_instancia"));
            ins.setX_nom_instancia((String) row.get("x_nom_instancia"));
            instancias.add(ins);
        }
    	return instancias;
    }
	
	@RequestMapping(value = "/obtReporteSecSIJ", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtReporteSecSIJ(Principal principal, @RequestBody Parametro param) {
		String sql = "SELECT DISTINCT usuario.x_nom_usuario,usuario.c_usuario,instancia.x_nom_instancia,instancia.c_instancia\r\n" + 
				"	FROM perfil,usuario,usuario_especialidad,organo_jurisdiccional,instancia,usuario_instancia  \r\n" + 
				"	WHERE ( usuario.c_perfil = perfil.codper ) and ( usuario.c_usuario = usuario_especialidad.c_usuario ) and  \r\n" + 
				"         ( organo_jurisdiccional.c_org_jurisd = usuario_especialidad.c_org_jurisd ) and  \r\n" + 
				"         ( usuario_instancia.c_usuario = usuario.c_usuario ) and  \r\n" + 
				"         ( instancia.c_instancia = usuario_instancia.c_instancia ) and  \r\n" + 
				"         (dba.perfil.codper = '02' OR dba.perfil.codper = '25' OR dba.perfil.codper = '31' OR  \r\n" + 
				"         dba.perfil.codper = '59' OR dba.perfil.codper = '60' OR dba.perfil.codper = '32') AND  \r\n" + 
				"         dba.usuario.l_activo = 'S' AND dba.organo_jurisdiccional.l_activo = 'S' AND  \r\n" + 
				"         dba.usuario_especialidad.l_activo = 'S' AND dba.usuario_especialidad.l_recibe_carga = 'S' AND  \r\n" + 
				"         dba.usuario.c_sede = ? AND dba.usuario_instancia.l_activo = 'S' AND  \r\n" + 
				"         dba.usuario_instancia.l_recibe_carga = 'S' and dba.instancia.c_instancia = ?";
    	List<Instancia> instancias = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate2.queryForList(sql, param.getCodigo(), param.getValor());
        System.out.println(rows.toString());
    	return instancias;
    }
}
