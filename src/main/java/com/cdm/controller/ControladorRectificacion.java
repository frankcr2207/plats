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
import com.cdm.service2.RecPartidaService;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

@Controller
public class ControladorRectificacion {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RecPartidaService recPartidaService;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarMySQLAlimentos());
    
    final String queryFTPAlimentos = "SELECT funcFTP(15) AS ftp, funcFTP(16) AS usuario, funcFTP(17) AS clave, concat('21') AS puerto";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }   
    
    private static String UPLOADED_FOLDER = "E:/temp_cdg/";
	
	@GetMapping("/RECTIFICACION")
	public String alimentos(Principal principal, Model model, String sede) {
        model.addAttribute("demandas", recPartidaService.getRecPartidas(principal.getName()));
        model.addAttribute("tipo", "RECTIFICACION DE PARTIDAS");
        model.addAttribute("formulario", "RECTIFICACION");
		return "vistas/listaRectificacion";
	}
	
	@GetMapping("/detalleRectificacion")
	public String detalleAlimentos(Model model, String id, String formulario, Principal principal) {
		String query = "";
		
		query = "select s_estado from rectificacion_partida where n_id_rectificacion = ?";
		String estado = this.jdbcTemplate2.queryForObject(query, String.class, id);
		
		if(estado.equals("P")) {
			query = "update rectificacion_partida set s_estado = 'T', c_usuario_csjar = ? where n_id_rectificacion = ?";
			this.jdbcTemplate2.update(query, principal.getName(), id);
		}
		
		query = "SELECT rp.c_sede FROM rectificacion_partida rp WHERE rp.n_id_rectificacion = ?";
		String sede = this.jdbcTemplate2.queryForObject(query,String.class, id);
		query = "SELECT rp.n_id_rectificacion, DATE_FORMAT(rp.f_sistema, '%d-%m-%Y %r') AS fechaRegistro, rp.s_documento_demandante, YEAR(NOW()) as anio, s.s_sede, rp.s_codigo as codigo, \r\n" + 
		"concat(rp.s_nombres_demandante, ' ', rp.s_paterno_demandante, ' ', rp.s_materno_demandante) AS nombresCompletos, date_format(rp.f_sistema_cdg, '%d-%m-%Y %r') as fechaAtencion,rp.* \r\n" + 
		"FROM rectificacion_partida rp INNER JOIN sede s on s.c_sede = rp.c_sede WHERE rp.n_id_rectificacion = ?";
		List<?> datos = this.jdbcTemplate2.queryForList(query, id);
		query = " SELECT * FROM instancia WHERE instancia.s_rectificacion='S' and c_sede = ?";
		List<?> instancias = this.jdbcTemplate2.queryForList(query, sede);
		query  = "select * from rectificacion_doc where n_id_rectificacion = ?";
		List<?> adjuntos = this.jdbcTemplate2.queryForList(query, id);
		String instancia = null;
		if(estado.equals("A")) {
			query = " SELECT i.x_nom_instancia FROM instancia i INNER JOIN rectificacion_partida a on a.c_instancia = i.c_instancia WHERE n_id_rectificacion = ?";
			instancia = this.jdbcTemplate2.queryForObject(query, String.class, id);
		}
		model.addAttribute("instancias", instancias);
		model.addAttribute("instancia", instancia);
		model.addAttribute("datos", datos);
		model.addAttribute("formulario", formulario);
		model.addAttribute("adjuntos", adjuntos);
		model.addAttribute("sesion", principal.getName());

		return "vistas/detalleRectificacion";
	}
	
	@GetMapping("/buscarRectificacion")
	public String buscarRectificacion(Model model, String texto, String sede, Principal principal) {
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
		}
		
        sql = "SELECT rp.n_id_rectificacion as id, DATE_FORMAT(rp.f_sistema, '%d-%m-%Y %r') AS fechaSistema, rp.s_documento_demandante, rp.s_estado as estado, \r\n" + 
        		"rp.s_nombres_demandante as nombres, rp.s_documento_demandante as documento, rp.c_usuario_csjar as usuario, rp.* \r\n" + 
        		"FROM rectificacion_partida rp WHERE rp.c_sede IN ("+ sede +") and (rp.s_nombres_demandante like ? or rp.x_expediente like ?)";
        List<?> demandas =  this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");

        model.addAttribute("demandas", demandas);
        model.addAttribute("tipo", "Coincidencias: " + texto);
        model.addAttribute("formulario", "RECTIFICACION");
		return "vistas/listaRectificacion";
	}
	
	@RequestMapping(value="/verDemandaRectificacion/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> getPDF3(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPAlimentos, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_rectificacion, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from rectificacion_partida where n_id_rectificacion = ?";
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
	
	@RequestMapping(value="/verificarAdjuntoRectificacion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> verificarPDFAlimentos(@RequestBody ValueCriteria vc, Principal principal) {
		FTPClient ftpClient = new FTPClient();
		Map<String,Object> map = new HashMap<String,Object>();
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPAlimentos, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select a.s_carpeta as ubicacion, a.s_file as nombre, CONCAT('_', date_format(now(), '%Y%m%d_%H%i%s')) as temporal from rectificacion_doc a WHERE a.n_id_doc_rectificacion = ?";
            
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
	
	@RequestMapping(value="/verAdjuntoRectificacion/{archivo}", method=RequestMethod.GET)
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
	
	@RequestMapping(value="/unirArchivosRectificacion/{id}/{archivos}", method=RequestMethod.GET)
	public ResponseEntity<Object> unirArchivosAlimentos(@PathVariable("id") String id, @PathVariable("archivos") String archivos) throws Exception {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPAlimentos, new FtpRowMapper()); 
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
            
            query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_rectificacion, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from rectificacion_partida where n_id_rectificacion = ?";
            @SuppressWarnings("unchecked")
            Archivo demanda = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), id);
            
            String demandaLocal = demanda.getUbicacion() + "/" + demanda.getNombre();
            String remoteFile = demandaLocal;
            File downloadFile2 = new File(UPLOADED_FOLDER + demanda.getNombre());
            
            if(!downloadFile2.exists()) {
            	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile2));
                ftpClient.retrieveFile(remoteFile, outputStream);
                outputStream.close();
            }
            inputPdfList.add(new FileInputStream(downloadFile2));

            String archivoFinal = "";
            for (Adjunto adjunto : adjuntos) {
            	query = "select a.s_carpeta as ubicacion, a.s_file as nombre, CONCAT(c.n_id_rectificacion, '_', date_format(c.f_sistema, '%Y%m%d_%H%i%s')) as temporal from rectificacion_partida c \r\n" + 
            			"inner join rectificacion_doc a on a.n_id_rectificacion = c.n_id_rectificacion WHERE a.n_id_doc_rectificacion = ?";
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
            
            archivoFinal = "PLATS_RP_" + archivoFinal + ".pdf";
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
	
	private int unirAdjuntos(String archivos, int id, String cargo, String archivoFinal) {
		
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPAlimentos, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        int flag;
        try {
	        ObjectMapper mapper = new ObjectMapper();
	        List<Adjunto> adjuntos = mapper.readValue(archivos, new TypeReference<List<Adjunto>>(){});
	        List<InputStream> inputPdfList = new ArrayList<InputStream>();
	        inputPdfList.add(new FileInputStream(UPLOADED_FOLDER + cargo));
	        
	        FTPClient ftpClient = new FTPClient();
	        ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        
	        for (Adjunto adjunto : adjuntos) {
	        	String sql = "select a.s_carpeta as ubicacion, a.s_file as nombre, CONCAT('_', date_format(now(), '%Y%m%d_%H%i%s')) as temporal from rectificacion_doc a WHERE a.n_id_doc_rectificacion = ?";
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
	
	@RequestMapping(value="/enviarRespuestaRectificacion", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestParam(value="idDemanda") int id, @RequestParam(value="idSede") String sede, @RequestParam(value="accion") String accion, @RequestParam(value="instancia") String instancia, @RequestParam(value="codigo") String codigo, @RequestParam(value="expediente") String expediente, @RequestParam(value="texto") String texto, @RequestParam(value="archivos") String archivos, Principal principal) throws InterruptedException {
		Map<String,Object> map = new HashMap<String,Object>();
		FTPClient ftpClient = new FTPClient();
		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				
				query = "select s_estado from rectificacion_partida where n_id_rectificacion = ?";
				String estadoSolicitud = this.jdbcTemplate2.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("P") || estadoSolicitud.equals("T")) {
					try {
						if(accion.equals("A")) {
							
							@SuppressWarnings("unchecked")
							Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPAlimentos, new FtpRowMapper()); 
							String server = ftp.getIp();
					        int port = ftp.getPuerto();
					        String user = ftp.getUsuario();
					        String pass = ftp.getClave();
					        
				            int sendRepo = 0;
				            String archivoFinal = null;
				            query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_rectificacion, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from rectificacion_partida where n_id_rectificacion = ?";
				            @SuppressWarnings("unchecked")
				            Archivo demanda = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), id);
				            
				            String demandaLocal = demanda.getUbicacion() + "/" + demanda.getNombre();
				            String remoteFile = demandaLocal;
				            File downloadFile2 = new File(UPLOADED_FOLDER + demanda.getNombre());
				            
				            if(!downloadFile2.exists()) {
				            	ftpClient.connect(server, port);
				                ftpClient.login(user, pass);
				                ftpClient.enterLocalPassiveMode();
				                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				            	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile2));
				                ftpClient.retrieveFile(remoteFile, outputStream);
				                outputStream.close();
				            }
					        String sql = "select concat('-', date_format(NOW(), '%d%m%Y-%H%i%s'))";
					    	archivoFinal = this.jdbcTemplate2.queryForObject(sql, String.class);
					    	archivoFinal = codigo + archivoFinal + ".pdf";
					        sendRepo = enviarRepositorio(sede, id, archivos, demanda.getNombre(), archivoFinal);

				            if(sendRepo == 200) {
								query = "update rectificacion_partida set s_estado = ?, s_codigo = ?, c_instancia = ?, x_expediente = ?, x_observacion_ingreso = ?, s_ip_pc = ?, f_sistema_cdg = now() where n_id_rectificacion = ?";
								this.jdbcTemplate2.update(query, accion, codigo, instancia, expediente, texto.toUpperCase(), ip, id);
								TimeUnit.SECONDS.sleep(1);
								map.put("Status", 200);
				            }
				            else {
				            	map.put("Status", sendRepo);
				            }
			            }
						else{
							query = "update rectificacion_partida set s_estado = ?, x_observacion_ingreso = ?, s_ip_pc = ?, f_sistema_cdg = now() where n_id_rectificacion = ?";
							this.jdbcTemplate2.update(query, accion, texto.toUpperCase(), ip, id);
							map.put("Status", 200);
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
	
	public int enviarRepositorio(String sede, int id, String archivos, String cargo, String archivoFinal) throws Exception {
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
            
			int union = unirAdjuntos(archivos, id, cargo, archivoFinal);
			
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
}
