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
import com.cdm.entities.Audiencia;
import com.cdm.entities.AudienciaCriteria;
import com.cdm.entities.Expediente;
import com.cdm.entities.Instancia;
import com.cdm.entities.Sala;
import com.cdm.entities.Sede;
import com.cdm.entities.ValueCriteria;
import com.cdm.zmappers.AudienciaRowMapper;
import com.cdm.zmappers.ExpedienteSIJRowMapper;

@RestController
@CrossOrigin
public class WebServiceAudienciasReglas {
	
	Conexion conexion = new Conexion();
	JdbcTemplate jdbcTemplate = new JdbcTemplate(conexion.ConectarMySQL());
	
	Conexion con = new Conexion();
	JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
	
	
	@RequestMapping(value = "/obtSala", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Sala> obtSala(@RequestBody ValueCriteria vc) {

    	String sql = "SELECT sa.s_nombre_sala FROM sala_audiencia sa WHERE sa.c_instancia = ?";
    	List<Sala> salas = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, vc.getId());
        for (@SuppressWarnings("rawtypes") Map row : rows) {
            Sala sala = new Sala();
            sala.setSala((String) row.get("s_nombre_sala"));
            salas.add(sala);
        }
    	return salas;
    }
	
    @RequestMapping(value = "/datosAudSIJ", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Audiencia datosAudSIJ(@RequestBody AudienciaCriteria audC) {

    	Audiencia audMeet= new Audiencia();
    	try {
	    	String sql = "SELECT instancia_expediente.n_unico, expediente.x_formato FROM instancia_expediente , instancia , expediente \r\n" + 
    			"WHERE ( instancia.c_distrito = instancia_expediente.c_distrito ) and ( instancia.c_provincia = instancia_expediente.c_provincia ) \r\n" + 
    			"and  ( instancia.c_instancia = instancia_expediente.c_instancia ) and ( instancia_expediente.n_unico = expediente.n_unico )\r\n" + 
    			"and  ( instancia_expediente.n_incidente = expediente.n_incidente ) and ( ( instancia_expediente.l_ultimo = 'S' ) \r\n" + 
    			"AND  ( instancia_expediente.l_ultimo_c_org = 'S' ) AND ( instancia_expediente.n_expediente = ? )\r\n" + 
    			"AND  ( instancia_expediente.n_ano = ?) AND ( instancia_expediente.n_incidente = ?) AND ( instancia_expediente.c_instancia = ? )  )";
    	
	    	List <?> lista = this.jdbcTemplate2.queryForList(sql, audC.getNumero(), audC.getAnio(), audC.getCuaderno(), audC.getInstancia());

	    	if(!lista.isEmpty()) {
	    		@SuppressWarnings("unchecked")
	    		Expediente exp = (Expediente) this.jdbcTemplate2.queryForObject(sql, new Object[] {audC.getNumero(), audC.getAnio(), audC.getCuaderno(), audC.getInstancia()} , new ExpedienteSIJRowMapper());
	    		sql = "SELECT FIRST x_formato AS num_expediente,\r\n" + 
	    				"		Convert(Char(8), f_inicio, 108) as h_inicio, Convert(Char(8), f_termino, 108) as h_termino, \r\n" + 
	    				"		x_audiencia, x_instancia, x_sala_audiencia, secuencia, n_ano, x_jueces,\r\n" + 
	    				"		x_secretario=(select x_nom_usuario from usuario NOHOLDLOCK where c_usuario=t.cod_secretario),\r\n" + 
	    				"		x_partes_1, x_partes_2, x_delito, x_observacion\r\n" + 
	    				"		FROM (SELECT a.secuencia, a.n_ano, a.f_inicio, a.f_termino, a.x_formato, a.x_observacion,\r\n" + 
	    				"		x_sala_audiencia=(select x_descripcion from sala_audiencia NOHOLDLOCK where n_sala=ap.n_sala),\r\n" + 
	    				"		x_instancia=(select x_nom_instancia from instancia NOHOLDLOCK where c_distrito=a.c_distrito and c_provincia=ap.c_provincia and c_instancia=ap.c_instancia),\r\n" + 
	    				"		cod_secretario=(select c_usuario from asignado_a NOHOLDLOCK where n_unico=a.n_unico and n_incidente=a.n_incidente and f_ingreso=a.f_ingreso and l_ultimo_instancia='S'),\r\n" + 
	    				"		x_partes_1=(select list(x_descripcion) from agenda_parte NOHOLDLOCK where n_ano=a.n_ano and secuencia=a.secuencia and l_activo='S' and l_tipo_parte in ('AGR','DTE','SOL')) ,\r\n" + 
	    				"		x_partes_2=(select list(x_descripcion) from agenda_parte NOHOLDLOCK where n_ano=a.n_ano and secuencia=a.secuencia and l_activo='S' and l_tipo_parte in ('IMP','INC','DDO')) ,\r\n" + 
	    				"		x_jueces=(select list(x_nom_usuario) from usuario u NOHOLDLOCK inner join agenda_usuario au NOHOLDLOCK on u.c_usuario=au.c_usuario and au.l_activo='S' and au.n_ano=a.n_ano and au.secuencia=a.secuencia and u.c_perfil in ('01','24')),\r\n" + 
	    				"		x_delito=(select x_desc_delito from delito NOHOLDLOCK where c_delito=a.c_delito),\r\n" + 
	    				"		x_audiencia=(select x_desc_audiencia from tipo_audiencia NOHOLDLOCK where c_audiencia=ap.c_audiencia)\r\n" + 
	    				"		FROM 	agenda a NOHOLDLOCK \r\n" + 
	    				"		INNER JOIN audiencia_programacion ap NOHOLDLOCK ON ap.n_sala=a.n_sala AND ap.c_audiencia=a.c_audiencia AND ap.n_programacion=a.n_programacion\r\n" + 
	    				"		INNER JOIN asistente_sala  asa NOHOLDLOCK ON asa.l_activo='S'	and asa.n_sala=ap.n_sala\r\n" + 
	    				"		WHERE \r\n" + 
	    				"		a.c_sede = ? AND (a.x_formato LIKE '%PE%' OR a.x_formato LIKE '%ED%')  AND \r\n" + 
	    				"		(a.n_unico = ? and a.n_incidente = ? and date(a.f_inicio) = ? ) and a.l_pendiente<>'A' ) t ORDER BY x_sala_audiencia,h_inicio ";

		    	@SuppressWarnings("unchecked")
				Audiencia audSIJ = (Audiencia) this.jdbcTemplate2.queryForObject(sql, new Object[] {audC.getSede(), exp.getN_unico(), audC.getCuaderno(), audC.getFecha()}, new AudienciaRowMapper());
		    	audMeet.setExpediente(exp.getX_formato());
		    	audMeet.setInstancia(audSIJ.getInstancia());
		    	audMeet.setTipo(audSIJ.getTipo());
		    	audMeet.setJueces(audSIJ.getJueces());
		    	audMeet.setParte1(audSIJ.getParte1());
		    	audMeet.setParte2(audSIJ.getParte2());
		    	audMeet.setDelito(audSIJ.getDelito());
		    	audMeet.setInicio(audC.getFecha() + 'T' +audSIJ.getInicio()+ ".000");

		    	sql = "SELECT sa.s_nombre_sala FROM sala_audiencia sa WHERE sa.c_instancia = ?";
		    	List<?> sala = this.jdbcTemplate.queryForList(sql, audC.getInstancia());
		    	if(sala.isEmpty()) {
		    		audMeet.setEnlace(getEnlace(audSIJ.getEnlace().toLowerCase().trim()).trim());
		    	}
		    	else {
		    		String jitsi = this.jdbcTemplate.queryForObject(sql, String.class, audC.getInstancia());
		    		audMeet.setEnlace(jitsi);
		    	}
	    	}
	    	else {
	    		audMeet.setExpediente("NN");
	    	}
    	}
    	catch(Exception ex) {
    		audMeet.setExpediente("XX");

    	}

    	return audMeet;
    }
    
    public String getEnlace(String enlace) {
    	String link = "";
    	String inicio = "meet.google";
    	int inicioPos = enlace.indexOf(inicio);
        if(inicioPos != -1)
        	link = enlace.substring(inicioPos, enlace.length());
        else
        	link = "";
    	return link;
    }
}
