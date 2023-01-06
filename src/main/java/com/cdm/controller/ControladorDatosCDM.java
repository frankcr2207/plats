package com.cdm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.cdm.entities.Calculo;
import com.cdm.entities.Derivacion;
import com.cdm.entities.Devolucion;
import com.cdm.entities.Ftp;
import com.cdm.entities.Parametro;
import com.cdm.entities.Proceso;
import com.cdm.entities.Resolucion;
import com.cdm.entities.Solicitud;
import com.cdm.entities.Turno;
import com.cdm.entities.Usuario;
import com.cdm.entities.ValueCriteria;
import com.cdm.interfaces.BASE64DecodedMultipartFile;
import com.cdm.utils.SmtpMailSender;
import com.cdm.utils.WebServiceReniec;
import com.cdm.zmappers.FtpRowMapper;
import com.cdm.zmappers.SolicitudRowMapper;

@Controller
public class ControladorDatosCDM {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    final String queryFTP = "SELECT funcFTP(8) AS ftp, funcFTP(9) AS usuario, funcFTP(10) AS clave, concat('21') AS puerto";
    final String queryFTPCDG = "SELECT funcFTP(14) AS ftp, funcFTP(15) AS usuario, funcFTP(16) AS clave, concat('21') AS puerto";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }   
	
    @Autowired
    private SmtpMailSender smtpMailSender;
    
	@SuppressWarnings("unchecked")
	@GetMapping("/obtenerDatos")
	@ResponseBody
	public Solicitud mostrarSolicitudes(String id) {
		String query = "select s.n_id, date_format(s.f_registro, '%d-%m-%Y %r') as fecha, s.s_correo_electronico, s.s_documento, s.s_nombres, s.s_apellidos, date_format(s.f_fecha_nacimiento, '%d-%m-%Y') as fNacimiento, s.s_celular, c.s_cdm, i.x_nom_instancia, s.n_expediente from solicitudes_cdm s inner join instancia i on i.c_instancia = s.c_instancia inner join cdm c on c.n_id_cdm = s.n_id_cdm where s.n_id = ?";
		Solicitud solicitud = (Solicitud) this.jdbcTemplate.queryForObject(query, new Object[] {id}, new SolicitudRowMapper());        
		return solicitud;
	}
	
	@RequestMapping(value = "/detalleObjetos", method = RequestMethod.GET)
	public String mostrarDetalle(Model model, @RequestParam(value="id") String id, Principal principal) {
		String query = "select p.s_perfil from usuarios u inner join perfil p on p.idperfil = u.idperfil where u.c_usuario = ?";
		String perfil = this.jdbcTemplate.queryForObject(query, String.class, principal.getName());
		query = "select s_estado from solicitudes_cdm where n_id = ?";
		String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class, id);
		if(estadoSolicitud.equals("N") && perfil.equals("ASISTENTE-CDM/CDG")) {
			query = "update solicitudes_cdm set s_estado = 'E', c_usuario = ? where n_id = ?";
			this.jdbcTemplate.update(query, principal.getName(), id);
		}
		query = "call `obtDetalleSolicitudCDM`(?)";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);
		query = "select f.form from solicitudes_cdm s inner join formulario f on f.n_id_formulario = s.n_id_formulario where n_id = ?";
		String form = this.jdbcTemplate.queryForObject(query, String.class, id);
		query = "select *, funcFechaLetras(f_fecha_resolucion) as fechaLetras from resolucion where id_der_cdm = ?";
		List<?> derivacion = this.jdbcTemplate.queryForList(query, id);
		query = "SELECT CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) AS usuarioAsistente, \r\n" + 
				"CONCAT(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) AS usuarioSecretario, date_format(r.f_fecha_derivacion, '%d-%m-%Y %r') as fechaDerivacion\r\n" + 
				"FROM solicitudes_cdm s, resolucion r, usuarios u, usuarios us WHERE u.c_usuario = s.c_usuario AND us.c_usuario = r.c_usuario\r\n" + 
				"AND s.n_id = ? AND r.id_der_cdm = ?";
		List<?> usuarios = this.jdbcTemplate.queryForList(query, id, id);
		model.addAttribute("detalle", detalle);
		model.addAttribute("tipoFormulario", form);
		model.addAttribute("sesion", principal.getName());
		model.addAttribute("derivacion", derivacion);
		model.addAttribute("usuarios", usuarios);
		return "vistas/detalleObjeto";
	}

	
	@RequestMapping(value = "/detalleCasilla", method = RequestMethod.GET)
	public String detalleCasilla(Model model, @RequestParam(value="id") String id, Principal principal) {
		String query = "";

		query = "SELECT u.*, c.*, DATE_FORMAT(c.f_sistema, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(u.s_nombres, ' ', u.s_apellidos) AS nombresCOmpletos, date_format(c.f_sistema_respuesta, '%d-%m-%Y %r') as fechaAtencion  \r\n" + 
				"FROM casillajudicial c INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario WHERE c.n_id_casillajudicial = ?";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);
		model.addAttribute("detalle", detalle);
		model.addAttribute("sesion", principal.getName());

		return "vistas/detalleCasilla";
	}
	
	
	@GetMapping("/consultaReniec")
	@ResponseBody
	public Usuario ConsultaReniec(String dni) {
		Usuario usuario = new Usuario();

		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(dni);
		if(datosReniec[0].equals("MBRE': 'no encontrado")) {
			usuario.setNombres("NO EXISTE!!");
			usuario.setPaterno("");
			usuario.setMaterno("");
			usuario.setPerfil("");
			usuario.setNacimiento("");
		}
		else {
			usuario.setNombres(datosReniec[0] + " " + datosReniec[1] + " " + datosReniec[2]);
			usuario.setPaterno(datosReniec[3]);
			usuario.setMaterno(datosReniec[4]);
			usuario.setPerfil(datosReniec[15]);
			usuario.setNacimiento(datosReniec[5]);
		}

		return usuario;
	}
	
	@RequestMapping(value="/enviarMensaje", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> fileUpload(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int idSolicitud, @RequestParam(value="correoRespuesta") String correo, @RequestParam(value="asuntoRespuesta") String asunto,  @RequestParam(value="textoRespuesta") String contenido, @RequestParam(value="cita") String cita, @RequestParam(value="agenda1") String agenda1, @RequestParam(value="agenda2") String agenda2, @RequestParam(value="espacio") String espacio, @RequestParam(value="descripcionAgenda") String descripcionAgenda, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				query = "select s_color from usuarios where c_usuario = ?";
				String color = this.jdbcTemplate.queryForObject(query, String.class, principal.getName());
				FTPClient ftpClient = new FTPClient();
				
				String ubicacionArchivo = "";
				String nombreArchivoOriginal = "";
				if(multipartFile.isEmpty()) {
					ubicacionArchivo = "";
					nombreArchivoOriginal = "";
				}
				else {
					query = "select date_format(f_registro, '%Y/%m') from solicitudes_cdm where n_id = " + idSolicitud;
					ubicacionArchivo = this.jdbcTemplate.queryForObject(query, String.class);
					nombreArchivoOriginal = "respuesta_" + multipartFile.getOriginalFilename();
				}
				
				query = "select s_estado from solicitudes_cdm where n_id = " + idSolicitud;
				String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class);
				if(!estadoSolicitud.equals("A")) {
	
					try {
						if(!multipartFile.isEmpty()) {
					        
					        @SuppressWarnings("unchecked")
							Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTP, new FtpRowMapper()); 
							String server = ftp.getIp();
					        int port = ftp.getPuerto();
					        String user = ftp.getUsuario();
					        String pass = ftp.getClave();
	
				            ftpClient.connect(server, port);
			
				            if (ftpClient.login(user, pass)) {
				            	ftpClient.enterLocalPassiveMode(); // important!
				            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				            	ftpClient.changeWorkingDirectory(ubicacionArchivo);
				                boolean result = ftpClient.storeFile(idSolicitud+"_respuesta_"+multipartFile.getOriginalFilename(), multipartFile.getInputStream());
				                if(result) {
				                	if(cita.equals("NO")) {
										//if(smtpMailSender.send(correo, asunto, contenido, multipartFile, encuesta(idSolicitud))) {
											query = "update solicitudes_cdm set s_estado = 'A', x_respuesta = ?, s_file_respuesta_pdf = ?, s_carpeta_respuesta_pdf = ?, s_cita = ?, ip_atendido = ?, f_fecha_atendido = now() where n_id = " + idSolicitud;
											this.jdbcTemplate.update(query, contenido, nombreArchivoOriginal, ubicacionArchivo, cita, ip);
										//}else map.put("Status", 350);
					                }else {
										//if (smtpMailSender.sendFechas(correo, asunto, contenido, agenda1, agenda2, multipartFile, encuesta(idSolicitud))) {
											query = "update solicitudes_cdm set s_estado = 'A', x_respuesta = ?, s_file_respuesta_pdf = ?, s_carpeta_respuesta_pdf = ?, ip_atendido = ?, s_cita = ?, f_agenda_1 = ?, f_agenda_2 = ?, t_espacio = ?, f_fecha_atendido = now() where n_id = " + idSolicitud;
											this.jdbcTemplate.update(query, contenido, nombreArchivoOriginal, ubicacionArchivo, ip, cita, agenda1, agenda2, espacio);
											query = "insert into agenda (id_solicitud, c_usuario, s_descripcion, f_evento_inicio, f_evento_fin, c_color) values (?,?,?,?, DATE_ADD(?, INTERVAL ? MINUTE),?)";
											this.jdbcTemplate.update(query, idSolicitud, principal.getName(), descripcionAgenda, agenda1, agenda1, espacio, color);
											query = "insert into agenda (id_solicitud, c_usuario, s_descripcion, f_evento_inicio, f_evento_fin, c_color) values (?,?,?,?, DATE_ADD(?, INTERVAL ? MINUTE),?)";
											this.jdbcTemplate.update(query, idSolicitud, principal.getName(), descripcionAgenda, agenda2, agenda2, espacio, color);
										//}else map.put("Status", 350);
					                }
				                }
				                else {
				                	map.put("Status", 300);
				                }
				                ftpClient.logout();
				                ftpClient.disconnect();
				            }
				            else
				            	map.put("Status", 300);
			            }
						else{
							
							if(cita.equals("NO")) {
								//if (smtpMailSender.sendSinArchivo(correo, asunto, contenido, encuesta(idSolicitud))) {
									query = "update solicitudes_cdm set s_estado = 'A', x_respuesta = ?, s_file_respuesta_pdf = ?, s_carpeta_respuesta_pdf = ?, s_cita = ?, ip_atendido = ?, f_fecha_atendido = now() where n_id = " + idSolicitud;
									this.jdbcTemplate.update(query, contenido, nombreArchivoOriginal, ubicacionArchivo, cita, ip);
								//}else map.put("Status", 350);
							}
							else {
								//if (smtpMailSender.sendFechasSinArchivo(correo, asunto, contenido, agenda1, agenda2, encuesta(idSolicitud))) {
									query = "update solicitudes_cdm set s_estado = 'A', x_respuesta = ?, s_file_respuesta_pdf = ?, s_carpeta_respuesta_pdf = ?, ip_atendido = ?, s_cita = ?, f_agenda_1 = ?, f_agenda_2 = ?, t_espacio = ?, f_fecha_atendido = now() where n_id = " + idSolicitud;
									this.jdbcTemplate.update(query, contenido, nombreArchivoOriginal, ubicacionArchivo, ip, cita, agenda1, agenda2, espacio);
									query = "insert into agenda (id_solicitud, c_usuario, s_descripcion, f_evento_inicio, f_evento_fin, c_color) values (?,?,?,?, DATE_ADD(?, INTERVAL ? MINUTE),?)";
									this.jdbcTemplate.update(query, idSolicitud, principal.getName(), descripcionAgenda, agenda1, agenda1, espacio, color);
									query = "insert into agenda (id_solicitud, c_usuario, s_descripcion, f_evento_inicio, f_evento_fin, c_color) values (?,?,?,?, DATE_ADD(?, INTERVAL ? MINUTE),?)";
									this.jdbcTemplate.update(query, idSolicitud, principal.getName(), descripcionAgenda, agenda2, agenda2, espacio, color);
								//}else map.put("Status", 350);
							}
						}
			        } catch (Exception e) {
			        	System.out.println(e.getMessage());
			        	map.put("Status", 500);
			        }
				}
				else{
					map.put("Status", 400);
				}
			}
			TimeUnit.SECONDS.sleep(1);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/derivarSolicitud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> asignarInstancia(@RequestBody Derivacion der, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "insert into resolucion (id_der_cdm, c_usuario, f_fecha_derivacion) values (?,?, NOW())";
				this.jdbcTemplate.update(sql, der.getId(), der.getSecretario());
				sql = "insert into movimientos (id_solicitud, s_tipo, f_fecha_movimiento, c_usuario_origen, c_usuario_destino) values (?,'DERIVACION',NOW(),?,?)";
				this.jdbcTemplate.update(sql, der.getId(), principal.getName(), der.getSecretario());
				this.jdbcTemplate.update("update solicitudes_cdm set s_estado = 'D' where n_id = ?", der.getId());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/resolverSolicitud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> resolverSolicitud(@RequestBody Resolucion res, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql;
				if(res.getFecha().equals("")) {
					sql = "update resolucion set n_resolucion = ?, f_fecha_resolucion = NOW(), s_fallo = ?, s_sumilla = ?, s_observacion = ? where id_der_cdm = ?";
					this.jdbcTemplate.update(sql, res.getNumero(), res.getFallo(), res.getSumilla(), res.getObservacion(), res.getId());
				}
				else {
					sql = "update resolucion set n_resolucion = ?, f_fecha_resolucion = ?, s_fallo = ?, s_sumilla = ?, s_observacion = ? where id_der_cdm = ?";
					this.jdbcTemplate.update(sql, res.getNumero(), res.getFecha(), res.getFallo(), res.getSumilla(), res.getObservacion(), res.getId());
				}
				sql = "select c_usuario from solicitudes_cdm where n_id = ?";
				String usuario = this.jdbcTemplate.queryForObject(sql, String.class, res.getId());
				sql = "insert into movimientos (id_solicitud, s_tipo, f_fecha_movimiento, c_usuario_origen, c_usuario_destino) values (?,'RESOLUCION',NOW(),?,?)";
				this.jdbcTemplate.update(sql, res.getId(), principal.getName(), usuario);
				this.jdbcTemplate.update("update solicitudes_cdm set s_estado = 'R' where n_id = ?", res.getId());
		        map.put("Status", 200);
		        
	        }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/devolverSolicitud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> devolverSolicitud(@RequestBody Devolucion dev, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "delete from resolucion where id_der_cdm = ?";
				this.jdbcTemplate.update(sql, dev.getId());
				sql = "select c_usuario from solicitudes_cdm where n_id = ?";
				String usuario = this.jdbcTemplate.queryForObject(sql, String.class, dev.getId());
				sql = "insert into movimientos (id_solicitud, s_tipo, s_observacion, f_fecha_movimiento, c_usuario_origen, c_usuario_destino) values (?,'DEVOLUCION',?,NOW(),?,?)";
				this.jdbcTemplate.update(sql, dev.getId(), dev.getMotivo(), principal.getName(), usuario);
				this.jdbcTemplate.update("update solicitudes_cdm set s_estado = 'X' where n_id = ?", dev.getId());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	public String encuesta(int id) {

		String link = "", sql = "";
		boolean flag = false;
		String CHAR_LOWER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	    String NUMBER = "0123456789";

	    String DATA_FOR_RANDOM_STRING = CHAR_UPPER + NUMBER;
	    SecureRandom random = new SecureRandom();

	    StringBuilder sb = new StringBuilder(10);
	    
	    do {
	    	for (int i = 0; i < 10; i++) {
		        int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
		        char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
		        sb.append(rndChar);
		    }
	    	sql = "select * from encuesta where s_id_encuesta = ?";
	    	List<?> lista = this.jdbcTemplate.queryForList(sql, id);
	    	if(lista.isEmpty()) {
	    		sql = "insert into encuesta (s_id_encuesta, n_id, s_estado) values (?,?,'P')";
	    		this.jdbcTemplate.update(sql, sb.toString(), id);
	    		flag = true;
	    	}
	    }
	    while(flag = false);
	    
	    link = "Encuesta: https://csjarequipa.pj.gob.pe/csjar/suga/encuesta/vista/encuesta.php?encuesta="+sb.toString();

		return link;
	}
	
}
