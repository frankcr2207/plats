package com.cdm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.entities.Archivo;
import com.cdm.entities.Ftp;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;

@Controller
public class ControladorAccesoInformacion {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	final String queryFTP = "SELECT funcFTP(26) AS ftp, funcFTP(27) AS usuario, funcFTP(28) AS clave, concat('21') AS puerto";
	
	private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }   
	
	@GetMapping("/ACCESOINFORMACION")
	public String devolucion(Model model, Principal principal) {
		try {
			String query = "select *, date_format(a.f_sistema, '%d-%m-%y %r') as fechaRegistro from acceso_informacion a where a.s_estado = 'P' order by a.f_sistema ASC";
			List<?> lista = this.jdbcTemplate.queryForList(query);
			model.addAttribute("tipo", "SOLIDITUDES");
			model.addAttribute("formulario", "ACCESOINFORMACION");
			model.addAttribute("solicitudes", lista);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "vistas/listaAccesoInformacion";  
	}
	
	@RequestMapping(value = "/detalleAccesoInformacion", method = RequestMethod.GET)
	public String detalleCasilla(Model model, @RequestParam(value="id") String id, Principal principal) {
		String query = "SELECT *, date_format(a.f_sistema, '%d-%m-%y %r') as fechaRegistro, date_format(a.f_sistema_respuesta, '%d-%m-%y %r') as fechaRespuesta  from acceso_informacion a where a.n_id_acceso = ?";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);
		model.addAttribute("detalle", detalle);
		model.addAttribute("sesion", principal.getName());

		return "vistas/detalleAccesoInformacion";
	}
	
	@RequestMapping(value = "/buscarAccesoInformacion", method = RequestMethod.GET)
	public String buscarAccesoInformacion(Model model, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, Principal principal) {
		String sql = "select *, date_format(a.f_sistema, '%d-%m-%y %r') as fechaRegistro from acceso_informacion a "
				+ " WHERE date(a.f_sistema) >= ? AND date(a.f_sistema) <= ? order by a.f_sistema desc ";
		List<?> lista = this.jdbcTemplate.queryForList(sql, inicio, fin);
		
		model.addAttribute("solicitudes", lista);
		model.addAttribute("tipo", "BUSQUEDA " + inicio + " - " + fin);
		model.addAttribute("formulario", "ACCESOINFORMACION");
		return "vistas/listaAccesoInformacion";  
	}
	
	@RequestMapping(value="/enviarRespuestaAI", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> fileUpload(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int idSolicitud, @RequestParam(value="accion") String accion, 
			@RequestParam(value="prorroga", required = false) String prorroga, @RequestParam(value="textoRespuesta") String contenido, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		if(prorroga == null)
			prorroga = "N";
		else
			prorroga = "S";
		
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
					query = "select date_format(f_sistema, '%Y/%m') from acceso_informacion where n_id_acceso = " + idSolicitud;
					ubicacionArchivo = this.jdbcTemplate.queryForObject(query, String.class);
					nombreArchivoOriginal = "AI_respuesta_" + idSolicitud + "_.pdf";
				}
				
				query = "select s_estado from acceso_informacion where n_id_acceso = " + idSolicitud;
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
				            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
				            	String creaRuta = LocalDateTime.now().format(formatter);
				            	ftpClient.makeDirectory(creaRuta);
				            	ftpClient.changeWorkingDirectory(ubicacionArchivo);
				            	
				                boolean result = ftpClient.storeFile("AI_respuesta_" + idSolicitud + "_.pdf", multipartFile.getInputStream());
				                if(result) {
									query = "update acceso_informacion set s_prorroga = ?, s_estado = ?, s_respuesta = ?, s_file_pdf_respuesta = ?, s_carpeta_pdf_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_acceso = ?";
									this.jdbcTemplate.update(query, prorroga, accion, contenido.toUpperCase(), nombreArchivoOriginal, ubicacionArchivo, ip, idSolicitud);
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
							query = "update acceso_informacion set s_prorroga = ?, s_estado = ?, s_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_acceso = ?";
							this.jdbcTemplate.update(query, prorroga, accion, contenido.toUpperCase(), ip, idSolicitud);	
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
	
	@RequestMapping(value="/verPDFAccesoInfo/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> verRespuestaPdfCJ(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTP, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_acceso, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from acceso_informacion where n_id_acceso = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);

            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());
            
            String adjunto = archivo.getUbicacion() + "/" + archivo.getNombre();
            String remoteFile1 = adjunto;

            File downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();
 
            if (success) {
                System.out.println("File #1 has been downloaded successfully.");
            }
            else {
            	System.out.println("No se pudo.");
            }
            
    		InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile1));
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", downloadFile1.getName()));
    		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    		headers.add("Pragma", "no-cache");
    		headers.add("Expires", "0");

    		responseEntity = ResponseEntity.ok().headers(headers)
    				.contentLength(downloadFile1.length())
    				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);

    		
	        } catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (ftpClient.isConnected()) {
	                    ftpClient.logout();
	                    ftpClient.disconnect();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
        
        return responseEntity;
	}
	
	@RequestMapping(value="/verPDFRespuestaAccesoInfo/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> verPDFRespuestaAccesoInfo(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTP, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf_respuesta as ubicacion, s_file_pdf_respuesta as nombre, concat(n_id_acceso, '_', date_format(f_sistema_respuesta, '%Y%m%d_%H%i%s')) as temporal from acceso_informacion where n_id_acceso = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);

            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());
            
            String adjunto = archivo.getUbicacion() + "/" + archivo.getNombre();
            String remoteFile1 = adjunto;

            File downloadFile1 = new File("E:/temp/" + archivo.getNombre());
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();
 
            if (success) {
                System.out.println("File AccesoInfo has been downloaded successfully.");
            }
            else {
            	System.out.println("File AccesoInfo no se pudo.");
            }
            
    		InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile1));
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", downloadFile1.getName()));
    		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    		headers.add("Pragma", "no-cache");
    		headers.add("Expires", "0");

    		responseEntity = ResponseEntity.ok().headers(headers)
    				.contentLength(downloadFile1.length())
    				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);

    		
	        } catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (ftpClient.isConnected()) {
	                    ftpClient.logout();
	                    ftpClient.disconnect();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
        
        return responseEntity;
	}
}
