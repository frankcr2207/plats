package com.cdm.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.entities.Ftp;
import com.cdm.zmappers.FtpRowMapper;

@Controller
public class ControladorCasillas {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	final String queryFTPCDG = "SELECT funcFTP(29) AS ftp, funcFTP(30) AS usuario, funcFTP(31) AS clave, concat('21') AS puerto";
    
	private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }  
	
	@GetMapping("/CASILLAS") 
	public String casillas(Model model, Principal principal) {
		try {
			List<?> lista = null;
			String sql = "SELECT c.*, DATE_FORMAT(c.f_sistema, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(u.s_nombres, ' ', u.s_apellidos) AS nombresCOmpletos FROM casillajudicial c \r\n" + 
					"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario WHERE c.s_estado = 'P'  ORDER BY c.f_sistema asc";
			lista = this.jdbcTemplate.queryForList(sql);

			model.addAttribute("documentos", lista);
			model.addAttribute("tipo", "SOLICITUDES DE CASILLAS");
			model.addAttribute("formulario", "CASILLAS");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaCasillas";
	}
	
	@RequestMapping(value = "/buscarCasilla", method = RequestMethod.GET)
	public String buscarCasillaFecha(Model model, @RequestParam(value="texto") String texto, Principal principal) {
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		List<?> lista = null;

		sql = "SELECT c.*, DATE_FORMAT(c.f_sistema, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(u.s_nombres, ' ', u.s_apellidos) AS nombresCOmpletos FROM casillajudicial c\r\n" + 
			"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario WHERE u.s_nombres like ? OR u.s_apellidos like ?";
		lista = this.jdbcTemplate.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
		
		model.addAttribute("documentos", lista);
		model.addAttribute("tipo", "BUSQUEDA " + texto);
		model.addAttribute("formulario", "CASILLAS");
		return "vistas/listaCasillas";  
	}
	
	@RequestMapping(value="/enviarRespuestaCJ", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaCJ(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int id, @RequestParam(value="accion") String accion, @RequestParam(value="textoRespuesta") String contenido, @RequestParam(value="numeroCasilla") String casilla, Principal principal) throws InterruptedException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				FTPClient ftpClient = new FTPClient();
				
				String ubicacionArchivo = "";
				String nombreArchivoOriginal = "";
				if(multipartFile.isEmpty()) {
					ubicacionArchivo = "";
					nombreArchivoOriginal = "";
				}
				else {
					query = "select date_format(f_sistema, '%Y/%m') from casillajudicial where n_id_casillajudicial = ?";
					ubicacionArchivo = this.jdbcTemplate.queryForObject(query, String.class, id);
					nombreArchivoOriginal = "CJ_respuesta_" + id + "_.pdf";
				}
				
				query = "select s_estado from casillajudicial where n_id_casillajudicial = ?";
				String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class, id);
				if(!estadoSolicitud.equals("A")) {
	
					try {
						if(!multipartFile.isEmpty()) {
					        
					        @SuppressWarnings("unchecked")
							Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
							String server = ftp.getIp();
					        int port = ftp.getPuerto();
					        String user = ftp.getUsuario();
					        String pass = ftp.getClave();
	
				            ftpClient.connect(server, port);
			
				            if (ftpClient.login(user, pass)) {
				            	ftpClient.enterLocalPassiveMode();
				            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				            	ftpClient.changeWorkingDirectory(ubicacionArchivo);
				                boolean result = ftpClient.storeFile("CJ_respuesta_" + id + "_.pdf", multipartFile.getInputStream());
				                if(result) {
									query = "update casillajudicial set s_estado = ?, s_respuesta = ?, s_file_pdf_respuesta = ?, s_carpeta_pdf_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_casillajudicial = ?";
									this.jdbcTemplate.update(query, accion, contenido, nombreArchivoOriginal, ubicacionArchivo, ip, id);
									map.put("Status", 200);
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
							query = "update casillajudicial set s_estado = ?, s_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_casillajudicial = ?";
							this.jdbcTemplate.update(query, accion, contenido, ip, id);
						}
						
						if(accion.equals("A")) {
							query = "update usuario set s_casilla_judicial = ?, s_abogado = 'S' where n_id_usuario = (select n_id_usuario from casillajudicial where n_id_casillajudicial = ?)";
							this.jdbcTemplate.update(query, casilla, id);
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
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		Thread.sleep(1000);
		return map;
	}
}
