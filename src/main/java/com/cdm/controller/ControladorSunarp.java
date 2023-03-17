package com.cdm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.RequestBody;
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
import com.cdm.entities.ValueCriteria;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

@Controller
public class ControladorSunarp {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarMySQLAlimentos());
    
    final String queryFTPSunarp = "SELECT funcFTP(7) AS ftp, funcFTP(8) AS usuario, funcFTP(9) AS clave, concat('21') AS puerto";
    
    private static String UPLOADED_FOLDER = "E:/temp_cdg/";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }
    
	@GetMapping("/SUNARPP")
	public String sunarp(Principal principal, Model model, String sede) {
		if(sede == null)
			sede = "0401";
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		
		if(perfil == 5) {
			sql = "select c_sede from sedeorganousuario s where s.c_usuario = ? GROUP BY s.c_sede";
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, principal.getName());
			sede = "";
	        for (Map row : rows) {
	        	sede = sede + row.get("c_sede") + ",";
	        }
	        sede = sede.substring(0, sede.length()-1);
	        
	        sql = "select *, date_format(se.f_sistema, '%d-%m-%Y %r') as fechaIngreso from sunarpsolicitud ss inner join sunarp_estado se on se.n_id_solicitud = ss.n_id_solicitud inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where ss.c_sede in ("+sede+") and ss.s_estado IN ('PM','T') and se.s_ultimo = 'S'";
		}
		else {
			sql = "select *, date_format(se.f_sistema, '%d-%m-%Y %r') as fechaIngreso from sunarpsolicitud ss inner join sunarp_estado se on se.n_id_solicitud = ss.n_id_solicitud  inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where ss.s_estado IN ('PM','T') and se.s_ultimo = 'S'";
		}
        
        List<?> demandas =  this.jdbcTemplate2.queryForList(sql);
        sql = "select * from sede";
        List<?> sedes = this.jdbcTemplate2.queryForList(sql);
        model.addAttribute("demandas", demandas);
        model.addAttribute("sedes", sedes);
        model.addAttribute("tipo", "SOLICITUDES SUNARP");
        model.addAttribute("formulario", "SUNARP");
		return "vistas/listaSunarp";
	}
	
	@GetMapping("/detalleSunarpp")
	public String detalleSunarp(Model model, String id, String formulario, Principal principal) {
		String query = "";
		
		query = "select s_estado from sunarpsolicitud where n_id_solicitud = ?";
		String estado = this.jdbcTemplate2.queryForObject(query, String.class, id);
		
		if(estado.equals("PM")) {
			query = "update sunarpsolicitud set s_estado = 'T', c_usuario_csjar = ? where n_id_solicitud = ?";
			this.jdbcTemplate2.update(query, principal.getName(), id);
		}
		
		query = "select *, date_format(se.f_sistema, '%d-%m-%Y %r') as fechaRegistro, YEAR(NOW()) as anio from sunarpsolicitud ss inner join sede s on s.c_sede = ss.c_sede inner join instancia i on i.c_instancia = ss.c_instancia inner join sunarp_estado se on se.n_id_solicitud = ss.n_id_solicitud where ss.n_id_solicitud = ? and se.s_estado = 'PM'";
		List<?> detalle = this.jdbcTemplate2.queryForList(query, id);
		query  = "select * from sunarp_doc where n_id_solicitud = ?";
		List<?> adjuntos = this.jdbcTemplate2.queryForList(query, id);
		List<?> atencion = null;
		if(estado.equals("PS")) {
			query = " SELECT *, date_format(se.f_sistema, '%d-%m-%Y %r') as fechaAtencion from sunarpsolicitud ss inner join sunarp_estado se on se.n_id_solicitud = ss.n_id_solicitud where ss.n_id_solicitud = ? and se.s_estado = 'PS'";
			atencion = this.jdbcTemplate2.queryForList(query, id);
		}
		model.addAttribute("atencion", atencion);
		model.addAttribute("adjuntos", adjuntos);
		model.addAttribute("detalle", detalle);
		model.addAttribute("formulario", formulario);
		model.addAttribute("sesion", principal.getName());

		return "vistas/detalleSunarp";
	}
	
	@GetMapping("/buscarDemandaSunarp")
	public String buscarDemandaSunarp(Model model, String tipo, String texto, String inicio, String fin, Principal principal) {
		String sql = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(sql, int.class, principal.getName());
		List<?> lista = null;
		
		sql = "select c_sede from sedeorganousuario s where s.c_usuario = ? GROUP BY s.c_sede";
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
				sql = "select *, date_format(f_sistema, '%d-%m-%Y %r') as fechaIngreso from sunarpsolicitud ss inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where ss.c_sede in (" + cadenaSedes + ") and ss.s_estado IN ('PM','T','PS') and (ss.s_nombre like ? or ss.x_expediente like ?)";
				lista = this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
			}
			else if (perfil == 15) {
				sql = "select *, date_format(f_sistema, '%d-%m-%Y %r') as fechaIngreso from sunarpsolicitud ss inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where ss.s_nombre like ? or ss.x_expediente like ? and ss.s_estado IN ('PM','T','PS')";
				lista = this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
			}
		}
		else if(tipo.equals("F")) {
			if(perfil == 5) {
				sql = "select *, date_format(f_sistema, '%d-%m-%Y %r') as fechaIngreso from sunarpsolicitud ss inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where ss.c_sede in (" + cadenaSedes + ") and ss.s_estado IN ('PM','T','PS') and date(ss.f_sistema) >= ? AND date(ss.f_sistema) <= ?";
				lista = this.jdbcTemplate2.queryForList(sql, inicio, fin);
			}
			else if (perfil == 15) {
				sql = "select *, (select date_format(se.f_sistema, '%d-%m-%Y %r') from sunarp_estado se where se.n_id_solicitud = ss.n_id_solicitud and se.s_estado = 'PM') as fechaIngreso from sunarpsolicitud ss inner join instancia i on i.c_instancia = ss.c_instancia inner join sede s on s.c_sede = ss.c_sede where date(ss.f_sistema) >= ? AND date(ss.f_sistema) <= ? and ss.s_estado IN ('PM','T','PS')";
				lista = this.jdbcTemplate2.queryForList(sql, inicio, fin);
			}
		}
		
		model.addAttribute("demandas", lista);
		model.addAttribute("tipo", "BUSQUEDA " + inicio + " - " + fin);
		model.addAttribute("formulario", "SUNARP");
		return "vistas/listaSunarp";  
	}
	
	@RequestMapping(value="/enviarRespuestaSunarp", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaSunarp(HttpServletResponse response, @RequestParam(value="idSolicitud") int id, @RequestParam(value="idSede") String sede, @RequestParam(value="codigo") String codigo, @RequestParam(value="respuesta") String respuesta, @RequestParam(value="archivos") String archivos, Principal principal) throws InterruptedException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				
				query = "select s_estado from sunarpsolicitud where n_id_solicitud = ?";
				String estadoSolicitud = this.jdbcTemplate2.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("T")) {
					try {
			            int sendRepo = 0;
			            String archivoFinal = null;
			           
				        String sql = "select concat('-', date_format(NOW(), '%d%m%Y-%H%i%s'))";
				    	archivoFinal = this.jdbcTemplate2.queryForObject(sql, String.class);
				    	archivoFinal = codigo + archivoFinal + ".pdf";
				        sendRepo = enviarRepositorio(sede, id, archivos, archivoFinal);

			            if(sendRepo == 200) {
							query = "update sunarpsolicitud set s_estado = 'PS' where n_id_solicitud = ?";
							this.jdbcTemplate2.update(query, id);
							this.jdbcTemplate2.update("update sunarp_estado set s_ultimo = 'N' where n_id_solicitud = ?", id);
							query = "insert into sunarp_estado (n_id_solicitud, x_observacion, s_ip_pc, s_estado, s_ultimo, f_sistema, c_usuario_csjar) values (?,?,?,'PS','S',now(),?)";
							this.jdbcTemplate2.update(query, id, respuesta.toUpperCase(), ip, principal.getName());
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
	
	@RequestMapping(value="/verificarPDFSunarp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> verificarPDFAlimentos(@RequestBody ValueCriteria vc, Principal principal) {
		FTPClient ftpClient = new FTPClient();
		Map<String,Object> map = new HashMap<String,Object>();
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPSunarp, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select a.s_carpeta_pdf as ubicacion, a.s_file_pdf as nombre, CONCAT('_', date_format(now(), '%Y%m%d_%H%i%s')) as temporal from sunarp_doc a WHERE a.n_id_sunarp_doc = ?";
            
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), vc.getId());

            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	
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
            Path filePath = Paths.get(UPLOADED_FOLDER + archivo.getNombre());
            long fileSize = Files.size(filePath); 
            if(fileSize == 0)
            	map.put("Status", "0");
            else
            	map.put("Status", archivo.getNombre());
        }
        catch(Exception ex) {
        	map.put("Status", "0");
        }
        return map;
		
	}
	
	@RequestMapping(value="/verPDFSunarp/{archivo}", method=RequestMethod.GET)
	private ResponseEntity<Object> verPDFAlimentos(@PathVariable("archivo") String archivo) throws FileNotFoundException, MessagingException {
		ResponseEntity<Object> responseEntity = null;
		
		File downloadFile1 = new File(UPLOADED_FOLDER + archivo);
		
		try {
		InputStreamResource resource = new InputStreamResource(new FileInputStream(UPLOADED_FOLDER + archivo));
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

        }		
		return responseEntity;
	}
	
	@RequestMapping(value="/unirArchivosSunarp/{id}/{archivos}", method=RequestMethod.GET)
	public ResponseEntity<Object> unirArchivosAlimentos(@PathVariable("id") String id, @PathVariable("archivos") String archivos) throws Exception {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPSunarp, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();

        ObjectMapper mapper = new ObjectMapper();
        List<Adjunto> adjuntos = mapper.readValue(archivos, new TypeReference<List<Adjunto>>(){});

        try {
       	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            List<InputStream> inputPdfList = new ArrayList<InputStream>();

            String archivoFinal = "";
            for (Adjunto adjunto : adjuntos) {
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, CONCAT('_', date_format(now(), '%Y%m%d_%H%i%s')) as temporal from sunarp_doc WHERE n_id_sunarp_doc = ?";
                @SuppressWarnings("unchecked")
                Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), adjunto.getValue());
                archivoFinal = archivo.getTemporal();
                String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
                String rutaLocal = UPLOADED_FOLDER + archivo.getNombre();
                File downloadFile1 = new File(UPLOADED_FOLDER + archivo.getNombre());
                if (!downloadFile1.exists()) {
                	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                    ftpClient.retrieveFile(remoteFile1, outputStream1);
                    outputStream1.close();
                }
                inputPdfList.add(new FileInputStream(rutaLocal));
            }
            
            archivoFinal = "PLATS_sunarp_" + archivoFinal + ".pdf";
            OutputStream outputStream =  new FileOutputStream(UPLOADED_FOLDER + archivoFinal);
            File downloadFile = new File(UPLOADED_FOLDER + archivoFinal);
            
            mergePdfFiles(inputPdfList, outputStream);
            
    		InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", downloadFile.getName()));
    		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    		headers.add("Pragma", "no-cache");
    		headers.add("Expires", "0");

    		responseEntity = ResponseEntity.ok().headers(headers)
    				.contentLength(downloadFile.length())
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
	
	private static void mergePdfFiles(List<InputStream> inputPdfList, OutputStream outputStream) throws Exception{
		
		Document document = new Document();
		PdfCopy copy = new PdfCopy(document, outputStream);
		document.open();
		PdfReader reader;
		int n;
		Iterator<InputStream> pdfIterator = inputPdfList.iterator();
		while(pdfIterator.hasNext()) {
			reader = new PdfReader(pdfIterator.next());
			n = reader.getNumberOfPages();
			for(int page = 0; page < n;) {
				copy.addPage(copy.getImportedPage(reader, ++page));
			}
			copy.freeReader(reader);
			reader.close();
		}
		outputStream.flush();
        document.close();
        outputStream.close();
        
    }

	public int enviarRepositorio(String sede, int id, String archivos, String archivoFinal) throws Exception {
		String sql = "select s_ip as ftp, s_user as usuario, s_password as clave, concat('21') AS puerto from ftp_cdg where c_sede = ?";
		//String sql = "select concat('172.28.0.3') as ftp, concat('mp') as usuario, concat('159753123456789') as clave, concat('21') AS puerto";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(sql, new FtpRowMapper(), sede); 
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
	        
			int union = unirAdjuntos(archivos, id, archivoFinal);
			
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
	
	private int unirAdjuntos(String archivos, int id, String archivoFinal) {
		
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPSunarp, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        int flag;
        try {
	        ObjectMapper mapper = new ObjectMapper();
	        List<Adjunto> adjuntos = mapper.readValue(archivos, new TypeReference<List<Adjunto>>(){});
	        List<InputStream> inputPdfList = new ArrayList<InputStream>();
	        
	        FTPClient ftpClient = new FTPClient();
	        ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        
	        for (Adjunto adjunto : adjuntos) {
	        	String sql = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, CONCAT('_', date_format(now(), '%Y%m%d_%H%i%s')) as temporal from sunarp_doc WHERE n_id_sunarp_doc = ?";
	            @SuppressWarnings("unchecked")
	            Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(sql, new ArchivoRowMapper(), adjunto.getValue());
	            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	            String rutaLocal = UPLOADED_FOLDER + archivo.getNombre();
	            File downloadFile1 = new File(UPLOADED_FOLDER + archivo.getNombre());
	            if (!downloadFile1.exists()) {
	            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
	            	ftpClient.retrieveFile(remoteFile1, outputStream1);
	            	outputStream1.close();
	            }
	            inputPdfList.add(new FileInputStream(rutaLocal));
	        }
	        
	        ftpClient.logout();
	        ftpClient.disconnect();
	        
	        OutputStream outputStream =  new FileOutputStream(UPLOADED_FOLDER + archivoFinal);
	        mergePdfFiles(inputPdfList, outputStream);
	        flag = 200;
        }
        catch(Exception ex) {
        	flag = 500;
        }
        return flag;
	}
	
}
