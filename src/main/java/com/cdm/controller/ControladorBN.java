package com.cdm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.config.Conexion;
import com.cdm.entities.Adjunto;
import com.cdm.entities.Archivo;
import com.cdm.entities.Ftp;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ControladorBN {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarMySQLAlimentos());
    
    final String queryFTPBanco = "SELECT funcFTP(1) AS ftp, funcFTP(2) AS usuario, funcFTP(3) AS clave, concat('21') AS puerto";
    
    private static String UPLOADED_FOLDER = "E:/temp_cdg/";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }
    
    @GetMapping("/BANCO")
	public String alimentos(Principal principal, Model model, String sede) {
    	if(sede == null) 
    		sede = "0401";
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		
		if(perfil == 5) {
			sql = "select c_sede from sedeorganousuario s where s.c_usuario = ? and s.n_activo = 1 GROUP BY s.c_sede";
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, principal.getName());
			sede = "";
        	for (Map row : rows) {
        		sede = sede + row.get("c_sede") + ",";
        	}
        	sede = sede.substring(0, sede.length()-1);
        	
        	sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede where ss.c_sede IN ("+sede+") and ss.s_estado IN ('A','R') AND ss.s_cdg IN ('N','T')";
		}
		else
		{
			sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede where ss.s_estado IN ('A','R') AND ss.s_cdg IN ('N','T')";
		}
		
        
        List<?> demandas =  this.jdbcTemplate2.queryForList(sql);
        sql = "select * from sede";
        List<?> sedes = this.jdbcTemplate2.queryForList(sql);
        model.addAttribute("solicitudes", demandas);
        model.addAttribute("sedes", sedes);
        model.addAttribute("tipo", "SOLICITUDES BANCO DE LA NACION");
        model.addAttribute("formulario", "BANCO");
		return "vistas/listaBanco";
	}
    
    @GetMapping("/detalleBanco")
	public String detalleBanco(Model model, String id, String formulario, Principal principal) {
		String query = "";
		
		query = "select s_cdg from bnsolicitud where n_id_solicitud = ?";
		String estado = this.jdbcTemplate2.queryForObject(query, String.class, id);
		
		if(estado.equals("N")) {
			query = "update bnsolicitud set s_cdg = 'T', c_usuario_csjar = ? where n_id_solicitud = ?";
			this.jdbcTemplate2.update(query, principal.getName(), id);
		}
		
		query = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaRegistro, date_format(ss.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion, YEAR(NOW()) as anio from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia where ss.n_id_solicitud = ?";
		List<?> detalle = this.jdbcTemplate2.queryForList(query, id);
		
		model.addAttribute("detalle", detalle);
		model.addAttribute("formulario", formulario);
		model.addAttribute("sesion", principal.getName());
		return "vistas/detalleBanco";
	}
    
    @GetMapping("/buscarDemandaBanco")
	public String buscarDemandaBanco(Model model, String tipo, String texto, String inicio, String fin, Principal principal) {
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		List<?> lista = null;
		
		sql = "select c_sede from sedeorganousuario s where s.c_usuario = ? and s.n_activo = 1 GROUP BY s.c_sede";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, principal.getName());
		String cadenaSedes = "";
		if(perfil == 5) {
			for (Map row : rows) {
        		cadenaSedes = cadenaSedes + row.get("c_sede") + ",";
        	}
        	cadenaSedes = cadenaSedes.substring(0, cadenaSedes.length()-1);
		}
		
		if(tipo.equals("T")) {
			if(perfil == 5) {
				sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso, date_format(ss.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion, YEAR(NOW()) as anio from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia where ss.c_sede in (" + cadenaSedes + ") and (ss.s_nombre like ? or ss.x_expediente like ?)";
				lista = this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
			}
			else if (perfil == 15) {
				sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso, date_format(ss.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion, YEAR(NOW()) as anio from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia where ss.s_nombre like ? or ss.x_expediente like ?";
				lista = this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
			}
		}
		else if(tipo.equals("F")) {
			if(perfil == 5) {
				sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso, date_format(ss.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion, YEAR(NOW()) as anio from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia where ss.c_sede in (" + cadenaSedes + ") and date(ss.f_sistema_respuesta) >= ? AND date(ss.f_sistema_respuesta) <= ?";
				lista = this.jdbcTemplate2.queryForList(sql, inicio, fin);
			}
			else if (perfil == 15) {
				sql = "select *, date_format(ss.f_sistema_respuesta, '%d-%m-%Y %r') as fechaIngreso, date_format(ss.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion, YEAR(NOW()) as anio from bnsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia where date(ss.f_sistema_respuesta) >= ? AND date(ss.f_sistema_respuesta) <= ?";
				lista = this.jdbcTemplate2.queryForList(sql, inicio, fin);
			}
		}
		
		model.addAttribute("solicitudes", lista);
		model.addAttribute("tipo", "BUSQUEDA " + inicio + " - " + fin);
		model.addAttribute("formulario", "BANCO");
		return "vistas/listaBanco";  
	}
    
    @RequestMapping(value="/enviarRespuestaBanco", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaBanco(HttpServletResponse response, @RequestParam(value="idSolicitud") int id, 
			@RequestParam(value="idSede") String sede, @RequestParam(value="codigo") String codigo, @RequestParam(value="accion") String accion, 
			@RequestParam(value="respuesta") String respuesta, Principal principal) throws InterruptedException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				
				query = "select s_cdg from bnsolicitud where n_id_solicitud = ?";
				String estadoSolicitud = this.jdbcTemplate2.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("T")) {
					try {
			            int sendRepo = 0;
			            String archivoFinal = null;
			           
				        String sql = "select concat('-', date_format(NOW(), '%d%m%Y-%H%i%s'))";
				    	archivoFinal = this.jdbcTemplate2.queryForObject(sql, String.class);
				    	archivoFinal = codigo + archivoFinal + ".pdf";
				    	
				    	if(accion.equals("S"))
				    		sendRepo = enviarRepositorio(sede, id, archivoFinal);
				    	else
				    		sendRepo = 200;
				        
				    	archivoFinal = (accion.equals("S") ? archivoFinal : null);
				        
			            if(sendRepo == 200) {
							query = "update bnsolicitud set s_cdg = ?, s_ingreso_cdg = ?, s_codigo = ?, f_sistema_cdg = NOW() where n_id_solicitud = ?";
							this.jdbcTemplate2.update(query, accion, respuesta.toUpperCase(), archivoFinal, id);
							TimeUnit.SECONDS.sleep(1);
							map.put("Status", 200);
			            }
			            else {
			            	map.put("Status", sendRepo);
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
    
    @RequestMapping(value="/verPDFBanco/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> getPDF3(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPBanco, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf_respuesta as ubicacion, s_file_pdf_respuesta as nombre, concat(n_id_solicitud, '_', date_format(f_sistema_respuesta, '%Y%m%d_%H%i%s')) as temporal from bnsolicitud where n_id_solicitud = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), id);
            
            String adjunto = archivo.getUbicacion() + "/" + archivo.getNombre();
            String remoteFile1 = adjunto;
            File downloadFile1 = new File(UPLOADED_FOLDER + archivo.getNombre());
            
            if(!downloadFile1.exists()) {
            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
                outputStream1.close();
                if (success)
                	System.out.println("DESCARGADO");
                else 
                	System.out.println("NO SE PUDO");
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
    
    public int enviarRepositorio(String sede, int id, String archivoFinal) throws Exception {
		String sql = "select s_ip as ftp, s_user as usuario, s_password as clave, concat('21') AS puerto from cdg_ftp_repositorio where c_sede = ?";
		//String sql = "select concat('172.28.0.3') as ftp, concat('mp') as usuario, concat('159753123456789') as clave, concat('21') AS puerto";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(sql, new FtpRowMapper(), sede); 
		String server = ftp.getIp();
	    int port = ftp.getPuerto();
	    String user = ftp.getUsuario();
	    String pass = ftp.getClave();
	    int resultado = 0;
	
	    FTPClient ftpClient = new FTPClient();
	    ftpClient.connect(server, port);
	    if (ftpClient.login(user, pass)) {
	    	ftpClient.enterLocalPassiveMode();
	    	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	    	ftpClient.changeWorkingDirectory("/");
	        
			int union = unirAdjuntos(id, archivoFinal);
			
			if(union == 200) {
	        	FileInputStream fis = new FileInputStream(UPLOADED_FOLDER + archivoFinal);
	            boolean result = ftpClient.storeFile(archivoFinal, fis);
	        	//boolean result = true;
	            if(result)
	            	resultado = 200;
	            else 
	            	resultado = 250;
	            
	            ftpClient.logout();
	            ftpClient.disconnect();
			}
			else {
				resultado = 270;
			}
	    }
	    else
	    	resultado = 290;
		
		return resultado;
	}
    
    private int unirAdjuntos(int id, String archivoFinal) {
		
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPBanco, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        int flag;
        try {
	        
	        FTPClient ftpClient = new FTPClient();
	        ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        
        	String sql = "select s_carpeta_pdf_respuesta as ubicacion, s_file_pdf_respuesta as nombre, date_format(now(), '%Y%m%d_%H%i%s') as temporal \r\n" + 
        		"from bnsolicitud WHERE n_id_solicitud = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(sql, new ArchivoRowMapper(), id);
            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
            File downloadFile1 = new File(UPLOADED_FOLDER + archivoFinal);
            if (!downloadFile1.exists()) {
            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            	ftpClient.retrieveFile(remoteFile1, outputStream1);
            	outputStream1.close();
	        }

	        ftpClient.logout();
	        ftpClient.disconnect();
	        
	        flag = 200;
        }
        catch(Exception ex) {
        	flag = 500;
        }
        return flag;
	}
}
