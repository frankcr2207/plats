package com.cdm.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.config.Conexion;
import com.cdm.entities.Abogado;
import com.cdm.entities.AbogadoCriteria;
import com.cdm.entities.Archivo;
import com.cdm.entities.Correo;
import com.cdm.entities.EventosEntity;
import com.cdm.entities.Expediente;
import com.cdm.entities.Ftp;
import com.cdm.entities.Instancia;
import com.cdm.entities.Parametro;
import com.cdm.utils.SmtpMailSender;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.ExpedienteRowMapper;
import com.cdm.zmappers.ExpedienteSIJRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ControladorNCPP {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
    @Autowired
    private SmtpMailSender smtpMailSender;
    
    public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }  
    
    public String generaClave() {
		Random r = new Random();
		Random s = new Random();
		String password = "";
		char letra;
		for(int i = 0; i < 3; i++) {	
			letra = (char)(r.nextInt(26) + 'A');
			password = password + String.valueOf(letra);
		}
		int Low = 100;
		int High = 999;
		int numero = s.nextInt(High-Low) + Low;
		password = password + String.valueOf(numero); 
		return password;
	}
    
	public String nombreCompleto(Principal principal) {
        String user = principal.getName();
        String sql = "select concat(s_nombres,' ',s_apellido_paterno,' ',s_apellido_materno) as nombres from usuarios where c_usuario = ?";
        String _nombre = this.jdbcTemplate.queryForObject(sql, String.class, user);
        return _nombre;
    }
	
	@GetMapping("/abogados") 
	public String abogados(Model model, Principal principal) {
		int tipoSolicitud = 1;
		String _nombre = this.nombreCompleto(principal);
		String query = "select a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos, a.s_activo, date_format(a.f_sistema, '%d-%m-%Y %r') as fechaRegistro from abogados a where a.s_activo = 'N'";
		List<?> lista = this.jdbcTemplate.queryForList(query);
		model.addAttribute("tipo", "LISTA DE ABOGADOS PARA INSCRIPCIÓN");
		model.addAttribute("nombre",_nombre);
		model.addAttribute("formulario", "abogados");
		model.addAttribute("abogados", lista);
		return "vistas/abogados";  
	}
	
	@RequestMapping(value = "/abogados/{estadoAbogado}", method = RequestMethod.GET)
	public String estadoAbogados(Model model, @PathVariable("estadoAbogado") String estadoAbogado, Principal principal) {
		String _nombre = this.nombreCompleto(principal);
		String query = "select a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos, a.s_activo, date_format(a.f_sistema, '%d-%m-%Y %r') as fechaRegistro from abogados a where a.s_activo = ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, estadoAbogado);
		model.addAttribute("tipo", "LISTA DE ABOGADOS");
		model.addAttribute("nombre",_nombre);
		model.addAttribute("formulario", "abogados");
		model.addAttribute("abogados", lista);
		return "vistas/abogados"; 
	}
	
	@RequestMapping(value = "/verAbogado/{formulario}/{dni}", method = RequestMethod.GET)
	public String detalleAbogados(Model model, @PathVariable("formulario") String formulario, @PathVariable("dni") String dni, Principal principal) {
		int tipoSolicitud = 1;
		String _nombre = this.nombreCompleto(principal);
		String query = "select a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos, date_format(a.f_sistema, '%d-%m-%Y %r') as fechaRegistro, c.s_colegio, a.s_colegiatura, a.s_telefono, a.s_email , a.s_activo, t.s_tipo_perfil, a.s_file_adjunto_pdf from abogados a inner join tipo_perfil t on t.n_id_tipo_perfil = a.n_id_tipo_perfil inner join colegio c on c.n_id_colegio = a.n_id_colegio WHERE a.s_dni = ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, dni);
		model.addAttribute("tipo", "DEVOLUCIÓN DE ANEXOS (Etapa postulatoria)");
		model.addAttribute("nombre",_nombre);
		model.addAttribute("formulario", "abogados");
		model.addAttribute("abogados", lista);
		return "vistas/detalleAbogado";  
	}
	
	@GetMapping("/solicitudExpedientes") 
	public String solicitudExpedientes(Model model, Principal principal) {
		int tipoSolicitud = 1;
		String _nombre = this.nombreCompleto(principal);
		String query = "select s.id_solicitud, a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos, date_format(s.f_fecha_solicitud, '%d-%m-%Y %r') as fechaRegistro, s.s_estado_solicitud from abogados a inner join solicitud_expedientes s on s.s_dni = a.s_dni where s.s_estado_solicitud = 'N' AND a.s_activo = 'A'";
		List<?> lista = this.jdbcTemplate.queryForList(query);
		model.addAttribute("tipo", "SOLICITUDES DE EXPEDIENTES");
		model.addAttribute("nombre",_nombre);
		model.addAttribute("formulario", "solicitudExpedientes");
		model.addAttribute("solicitudes", lista);
		return "vistas/listaSolicitudesExpedientes";  
	}
	
	@RequestMapping(value = "/detalleSolicitudExpedientes/{id}", method = RequestMethod.GET)
	public String mostrarDetalle(Model model, @PathVariable("id") String id, Principal principal) {
		String query = "select d.id_correlativo, concat('EXP: ', d.n_expediente, '-',d.n_anio, '-',d.n_cuaderno, ' - ',i.x_nom_instancia) as expedienteCompleto, d.s_estado_expediente from detalle_solicitud_expedientes d inner join instancia i on i.c_instancia = d.id_instancia where id_solicitud_expedientes = ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, id);
		query = "select distinct a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos from abogados a \r\n" + 
				"inner join solicitud_expedientes s ON s.s_dni = a.s_dni WHERE s.id_solicitud = ?";
		List<?> solicitante = this.jdbcTemplate.queryForList(query, id);
		model.addAttribute("expedientes", lista);
		model.addAttribute("solicitante", solicitante);
		model.addAttribute("tipo", "LISTA DE EXPEDIENTES REQUERIDOS");
		model.addAttribute("formulario", "solicitudExpedientes");
		model.addAttribute("idSolicitud", id);
		model.addAttribute("sesion", principal.getName());
		return "vistas/listaExpedientes";
	}
	
	@RequestMapping(value = "/buscarExpediente/{id}/{dni}", method = RequestMethod.GET)
	public String buscarExpediente(Model model, @PathVariable("id") String id, @PathVariable("dni") String dni, Principal principal) {
		String query;
		int expEncontrado = 1;
		List<?> partesExp;
		query = "select d.id_correlativo, d.id_solicitud_expedientes, d.n_expediente, d.n_anio, d.n_cuaderno, d.id_instancia, i.x_nom_instancia, d.s_nombre_parte, d.s_nombre_secretario from detalle_solicitud_expedientes d inner join instancia i on i.c_instancia = d.id_instancia where d.id_correlativo = ?";
		@SuppressWarnings("unchecked")
		Expediente expediente = (Expediente) this.jdbcTemplate.queryForObject(query, new ExpedienteRowMapper(), id);
		query = "SELECT instancia_expediente.n_unico, instancia_expediente.n_incidente, expediente.x_formato \r\n" + 
				"FROM instancia_expediente , instancia , expediente  \r\n" + 
				"WHERE ( instancia.c_distrito = instancia_expediente.c_distrito ) \r\n" + 
				"and  ( instancia.c_provincia = instancia_expediente.c_provincia ) \r\n" + 
				"and  ( instancia.c_instancia = instancia_expediente.c_instancia ) \r\n" + 
				"and  ( instancia_expediente.n_unico = expediente.n_unico ) \r\n" + 
				"and  ( instancia_expediente.n_incidente = expediente.n_incidente ) \r\n" + 
				"and  ( ( instancia_expediente.l_ultimo = 'S' ) \r\n" + 
				"AND  ( instancia_expediente.l_ultimo_c_org = 'S' ) \r\n" + 
				"AND  ( instancia_expediente.n_expediente = ? ) \r\n" + 
				"AND  ( instancia_expediente.n_ano = ?) \r\n" + 
				"AND  ( instancia_expediente.n_incidente = 0) \r\n" + 
				"AND  ( instancia_expediente.c_instancia = ? )  )    ";
		List<?> lista = this.jdbcTemplate2.queryForList(query, expediente.getNumero(), expediente.getAnio(), expediente.getId_instancia());
		
		if(lista.size() == 0) {
			expEncontrado = 0;
			query = "update detalle_solicitud_expedientes set s_estado_expediente = 'N' where id_correlativo = ?";
			this.jdbcTemplate.update(query, id);
		}
		else {
			@SuppressWarnings("unchecked")
			Expediente expedienteSIJ = (Expediente) this.jdbcTemplate2.queryForObject(query, new ExpedienteSIJRowMapper(), expediente.getNumero(), expediente.getAnio(), expediente.getId_instancia());
			query = "select * from expedientes where s_dni = ? and n_unico = ?";
			List<?> registrado = this.jdbcTemplate.queryForList(query, dni, expedienteSIJ.getN_unico());

			if(registrado.size() != 0) {
				expEncontrado = 2;
				query = "update detalle_solicitud_expedientes set s_estado_expediente = 'A' where id_correlativo = ?";
				this.jdbcTemplate.update(query, id);
			}
			else {
				//query = "select nombre_parte = (x_nombres+' '+x_ape_paterno+' '+x_ape_materno) from parte where n_incidente = 0 and n_unico = ?";
				query = "select nombreCompletoParte = (x_nombres+' '+x_ape_paterno+' '+x_ape_materno) from parte where n_incidente = 0 and n_unico = ?";
				partesExp = this.jdbcTemplate2.queryForList(query, expedienteSIJ.getN_unico());
				expEncontrado = 1;
				model.addAttribute("expedienteSIJ", expedienteSIJ);
				model.addAttribute("partesExp", partesExp);
			}
		}
		model.addAttribute("expEncontrado", expEncontrado);
		model.addAttribute("expediente", expediente);
		model.addAttribute("formulario", "solicitudExpedientes");
		model.addAttribute("dni", dni);
		return "vistas/datosSIJ";
	}
	
	@RequestMapping(value = "/admitirExpediente/{solicitud}/{correlativo}/{exp}/{formato}/{dniSol}", method = RequestMethod.GET)
	public String admitirExp(Model model, @PathVariable("solicitud") String solicitud, @PathVariable("correlativo") String correlativo,  @PathVariable("exp") String unico, @PathVariable("formato") String formato, @PathVariable("dniSol") String dni, Principal principal) {
		String ip = getClientIp();
		String query = "update detalle_solicitud_expedientes set s_estado_expediente = 'A' where id_correlativo = ?";
		this.jdbcTemplate.update(query, correlativo);
		query = "insert into expedientes (s_dni, n_unico, x_formato, s_activo, c_usuario, f_sistema, c_ip) values (?,?,?,'S',?,now(),?)";
		//int id_insert = this.jdbcTemplate.update(query, dni, unico, formato, principal.getName(), ip, Statement.RETURN_GENERATED_KEYS);
		this.jdbcTemplate.update(query, dni, unico, formato, principal.getName(), ip);
		query = "select d.id_correlativo, concat('EXP: ', d.n_expediente, '-',d.n_anio, '-',d.n_cuaderno, ' - ',i.x_nom_instancia) as expedienteCompleto, d.s_estado_expediente from detalle_solicitud_expedientes d inner join instancia i on i.c_instancia = d.id_instancia where id_solicitud_expedientes = ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, solicitud);
		query = "select distinct a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos from abogados a \r\n" + 
				"inner join solicitud_expedientes s ON s.s_dni = a.s_dni WHERE s.id_solicitud = ?";
		List<?> solicitante = this.jdbcTemplate.queryForList(query, solicitud);
		model.addAttribute("expedientes", lista);
		model.addAttribute("solicitante", solicitante);
		return "vistas/listaExpedientes";
	}
	
	@RequestMapping(value = "/rechazarExpediente/{solicitud}/{correlativo}/{dniSol}", method = RequestMethod.GET)
	public String rechazarExpediente(Model model, @PathVariable("solicitud") String solicitud, @PathVariable("correlativo") String correlativo, @PathVariable("dniSol") String dni, Principal principal) {
		String ip = getClientIp();
		String query = "update detalle_solicitud_expedientes set s_estado_expediente = 'R' where id_correlativo = ?";
		this.jdbcTemplate.update(query, correlativo);
		query = "select d.id_correlativo, concat('EXP: ', d.n_expediente, '-',d.n_anio, '-',d.n_cuaderno, ' - ',i.x_nom_instancia) as expedienteCompleto, d.s_estado_expediente from detalle_solicitud_expedientes d inner join instancia i on i.c_instancia = d.id_instancia where id_solicitud_expedientes = ?";
		List<?> lista = this.jdbcTemplate.queryForList(query, solicitud);
		query = "select distinct a.s_dni, concat(a.s_nombres, ' ', a.s_apellidos) as nombresCompletos from abogados a \r\n" + 
				"inner join solicitud_expedientes s ON s.s_dni = a.s_dni WHERE s.id_solicitud = ?";
		List<?> solicitante = this.jdbcTemplate.queryForList(query, solicitud);
		model.addAttribute("expedientes", lista);
		model.addAttribute("solicitante", solicitante);
		return "vistas/listaExpedientes";
	}
	
	/*@RequestMapping(value="/enviarMensajeAbogado", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> fileUpload(HttpServletResponse response, @RequestParam(value="confirma") String confirma, @RequestParam(value="dniAbogado") String dniAbogado, @RequestParam(value="correoRespuesta") String correo, @RequestParam(value="asuntoRespuesta") String asuntoRespuesta,  @RequestParam(value="textoRespuestaAbogado") String textoRespuestaAbogado,  Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		int estado = 1;
		String ip = getClientIp();
		try {
			String query;

			query = "select s_activo from abogados where s_dni = ?";
			String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class, dniAbogado);
			if(!estadoSolicitud.equals("A")) {

				try {
					String clave = null;
					clave = generaClave();
                	smtpMailSender.sendAbogado(correo, asuntoRespuesta, textoRespuestaAbogado, confirma, dniAbogado, clave);
					if(confirma.equals("SI")) {
						query = "update abogados set s_activo = 'A', s_password = MD5(?), c_usuario = ?, c_ip = ?, f_sistema_activo = now() where s_dni = ?";
						this.jdbcTemplate.update(query, clave, principal.getName(), ip, dniAbogado);
					}

		        } catch (Exception e) {
		        	System.out.println(e.getMessage());
		        	estado = 0;
		        }
			}
			else{
				estado = 2;
			}
	        if(estado == 1)
	        	map.put("Status", 200);
	        else if(estado == 2)
	        	map.put("Status", 400);
	        else
	        	map.put("Status", 500);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}*/
	
	@RequestMapping(value="/actAbo", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> actParam(@RequestBody AbogadoCriteria abogado, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String query = "update abogados set s_activo = ? where s_dni = ?";
			this.jdbcTemplate.update(query, abogado.getEstado(), abogado.getDni());
			map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	/*@RequestMapping(value="/nuevaClaveAbo", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> nuevaClaveAbo(@RequestBody Abogado abogado, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String clave = null;
			clave = generaClave();
			String mensaje ="Estimado(a) " + abogado.getNombres() + ", a solicitud suya se procede a generar una nueva clave de acceso.";
			smtpMailSender.sendAbogado(abogado.getEmail(), "CSJAR - NCPP - NUEVA CLAVE DE ACCESO ", mensaje, "NC", abogado.getDni(), clave);
			String query = "update abogados set s_password = MD5(?) where s_dni = ?";
			this.jdbcTemplate.update(query, clave, abogado.getDni());
			map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/enviarMensajeSolicitud", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarMensajeSolicitud(HttpServletResponse response, @RequestParam(value="idSolicitud") String idSolicitud, @RequestParam(value="nombresCompletos") String nombresCompletos, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		int estado = 1;
		String ip = getClientIp();
		try {
			String query = "select * from detalle_solicitud_expedientes where s_estado_expediente = 'I' and id_solicitud_expedientes = ?";
			List<?> lista = this.jdbcTemplate.queryForList(query, idSolicitud);
			if(lista.size() == 0) {
				try {
					lista = null;
					query = "select CONCAT(d.n_expediente, '-', d.n_anio, '-', d.n_cuaderno, '  ', i.x_nom_instancia, '   -->   ', funcEstado(d.s_estado_expediente)) \r\n" + 
							"AS expCompleto \r\n" + 
							"from detalle_solicitud_expedientes d \r\n" + 
							"INNER JOIN instancia i ON i.c_instancia = d.id_instancia\r\n" + 
							"where id_solicitud_expedientes = ? ORDER BY d.s_estado_expediente ASC";
					lista = this.jdbcTemplate.queryForList(query, idSolicitud);
					System.out.println(lista.size());
					query = "select s_email from abogados a inner JOIN solicitud_expedientes s ON s.s_dni = a.s_dni WHERE s.id_solicitud = ?";
					String correo = this.jdbcTemplate.queryForObject(query, String.class, idSolicitud);
					System.out.println(correo);
					String mensaje = "Estimado(a) " + nombresCompletos + ", en el siguiente listado se indica el estado de los expedientes solicitados para consulta virtual. En caso de tener expedientes rechazados o no encontrados, genere una nueva solicitud y asegúrese de ingresar datos pertinentes del o los expedientes.";
                	smtpMailSender.expedienteSolicitud(correo, "CSJAR NCPP - Respuesta de estados de expedientes solicitados", mensaje, lista);
					query = "update solicitud_expedientes set s_estado_solicitud = 'A', c_usuario = ?,  f_fecha_atencion = now() where id_solicitud = ?";
					this.jdbcTemplate.update(query, principal.getName(), idSolicitud);
					map.put("Status", 200);
		        } catch (Exception e) {
		        	System.out.println(e.getMessage());
		        	map.put("Status", 500);
		        }
			}
			else{
				map.put("Status", 400);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}*/
	
	
}
