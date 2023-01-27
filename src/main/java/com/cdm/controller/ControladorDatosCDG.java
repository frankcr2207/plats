package com.cdm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.cdm.entities.Adjunto;
import com.cdm.entities.Archivo;
import com.cdm.entities.Calculo;
import com.cdm.entities.Ftp;
import com.cdm.entities.Parametro;
import com.cdm.entities.Turno;
import com.cdm.entities.ValueCriteria;
import com.cdm.service1.CdgDocumentoService;
import com.cdm.service1.FtpCdgService;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class ControladorDatosCDG {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private FtpCdgService ftpCdgService;
	
	public ControladorDatosCDG(FtpCdgService ftpCdgService) {
		this.ftpCdgService = ftpCdgService;
	}
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarSybase());
    final String queryFTP = "SELECT funcFTP(8) AS ftp, funcFTP(9) AS usuario, funcFTP(10) AS clave, concat('21') AS puerto";
    final String queryFTPCDG = "SELECT funcFTP(14) AS ftp, funcFTP(15) AS usuario, funcFTP(16) AS clave, concat('21') AS puerto";
    
    private static String UPLOADED_FOLDER = "E:/temp_cdg/";
    //private static String UPLOADED_FOLDER = "E:/temp_cdg_prueba/";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }   
    
	@RequestMapping(value = "/detalleDocumento", method = RequestMethod.GET)
	public String detalleDocumento(Model model, @RequestParam(value="id") String id, @RequestParam(value="formulario") String form, Principal principal) {
		String query = "";
		
		query = "select x_version from usuarios where c_usuario = ?";
		int version = this.jdbcTemplate.queryForObject(query, int.class, principal.getName());
		//int version = 1;

		query = "SELECT c.*, c.n_id_cdg, date_format(c.f_sistema, '%d-%m-%Y %r') as fechaRegistro, u.s_documento, u.s_celular, concat(u.s_nombres, ' ', u.s_apellidos) as nombresCompletos, t.s_tipo_documento, c.x_expediente, c.c_sede, s.s_sede, i.x_nom_instancia, c.s_estado, concat(us.s_nombres, ' ', us.s_apellido_paterno, ' ', us.s_apellido_materno) as nombresAsignado,  \r\n" + 
				"date_format(f_sistema_respuesta, '%d-%m-%Y %r') as fechaAtencion, funcFTP(19) as asunto from cdg c INNER JOIN sede s ON s.c_sede = c.c_sede  INNER JOIN instancia i ON i.c_instancia = c.c_instancia \r\n" + 
				"INNER JOIN usuario u ON u.n_id_usuario = c.n_id_usuario INNER JOIN usuarios us ON us.c_usuario = c.c_usuario_asignado \r\n" + 
				"INNER JOIN tipo_documento t ON t.n_id_tipo_documento = c.n_id_tipo_documento where c.n_id_cdg = ?";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);
		query  = "SELECT u.c_usuario, CONCAT(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as nombresCompletos FROM sedeorganousuario s \r\n" + 
				"INNER JOIN usuarios u ON u.c_usuario = s.c_usuario\r\n" + 
				"WHERE u.idperfil = 5 and s.n_id_organo = (select n_id_organo from cdg where n_id_cdg = ?) AND s.c_sede = (select c_sede from cdg where n_id_cdg = ?) AND s.c_usuario <> (SELECT c_usuario_asignado FROM cdg WHERE n_id_cdg = ?)";
		List<?> reasigna = this.jdbcTemplate.queryForList(query, id, id, id);
		query  = "select * from cdg_archivos where n_id_cdg = ?";
		List<?> adjuntos = this.jdbcTemplate.queryForList(query, id);
		model.addAttribute("usuarios", reasigna);
		model.addAttribute("adjuntos", adjuntos);
		model.addAttribute("detalle", detalle);
		model.addAttribute("formulario", form);
		model.addAttribute("sesion", principal.getName());
		
		String vista;
		
		if(version == 1)
			vista = "vistas/detalleDocumento";
		else
			vista = "vistas/detalleDocumento2";

		return vista;
	}
	
	//Original
	@RequestMapping(value="/enviarRespuestaCDGNew", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int id, @RequestParam(value="idSede") String sede, @RequestParam(value="accion") String accion, @RequestParam(value="repositorio") String repositorio, @RequestParam(value="textoRespuesta") String contenido, @RequestParam(value="archivos") String archivos, Principal principal) throws InterruptedException {
	//public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int id, @RequestParam(value="accion") String accion, @RequestParam(value="textoRespuesta") String contenido, Principal principal) throws InterruptedException {
		Map<String,Object> map = new HashMap<String,Object>();

		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				String ubicacionArchivo = "";
				String nombreArchivoOriginal = "";
				if(multipartFile.isEmpty()) {
					ubicacionArchivo = "";
					nombreArchivoOriginal = "";
				}
				else {
					query = "select date_format(f_sistema, '%Y/%m') from cdg where n_id_cdg = ?";
					ubicacionArchivo = this.jdbcTemplate.queryForObject(query, String.class, id);
					nombreArchivoOriginal = id + "_cdg_respuesta_" + multipartFile.getOriginalFilename();
				}
				
				query = "select s_estado from cdg where n_id_cdg = ?";
				String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("P")) {
					try {
						if(!multipartFile.isEmpty() && accion.equals("A")) {
							
							@SuppressWarnings("unchecked")
							Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
							String server = ftp.getIp();
					        int port = ftp.getPuerto();
					        String user = ftp.getUsuario();
					        String pass = ftp.getClave();
					        
							byte[] bytes = multipartFile.getBytes();
							String cargo = "PLATS_mpe_" + id + ".pdf";
				            Path path = Paths.get(UPLOADED_FOLDER + cargo);
				            Files.write(path, bytes);
				            int sendRepo = 0;
				            String archivoFinal = null;
				            
				            if(repositorio.equals("A")) {
				            	
				            	String codDigitalizacion = leerDesdePDF(UPLOADED_FOLDER + cargo, 1, "Cod.", "lizacion").trim();
				            	if(codDigitalizacion.equals("ERROR") || codDigitalizacion.length() != 25) {
				            		sendRepo = 350;
					            }
					            else {
					            	String sql = "select concat('-', date_format(NOW(), '%d%m%Y-%H%i%s'))";
					    			archivoFinal = this.jdbcTemplate.queryForObject(sql, String.class);
					    			archivoFinal = codDigitalizacion + archivoFinal + ".pdf";
					            	sendRepo = enviarRepositorio(sede, id, archivos, cargo, archivoFinal);
					            }
				            }
				            else {
				            	sendRepo = 200;
				            }
				            
				            if(sendRepo == 200) {
				            	FTPClient ftpClient = new FTPClient();
					        	ftpClient.connect(server, port);
					            if (ftpClient.login(user, pass)) {
					            	ftpClient.enterLocalPassiveMode();
					            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					            	ftpClient.changeWorkingDirectory(ubicacionArchivo);
					                boolean result = ftpClient.storeFile(id+"_cdg_respuesta_"+multipartFile.getOriginalFilename(), multipartFile.getInputStream());
					                if(result) {
										query = "update cdg set s_estado = ?, s_respuesta = ?, s_file_pdf_respuesta = ?, s_carpeta_pdf_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_cdg = ?";
										this.jdbcTemplate.update(query, accion, contenido, nombreArchivoOriginal, ubicacionArchivo, ip, id);
										map.put("Status", 200);
										map.put("Archivo", archivoFinal);
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
				            else {
				            	map.put("Status", sendRepo);
				            }
			            }
						else{
							query = "update cdg set s_estado = ?, s_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_cdg = ?";
							this.jdbcTemplate.update(query, accion, contenido, ip, id);
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
		Thread.sleep(2000);
		return map;
	}
	
	//Original
	@RequestMapping(value="/enviarRespuestaCDG", method = RequestMethod.POST)
	//public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int id, @RequestParam(value="idSede") int sede, @RequestParam(value="accion") String accion, @RequestParam(value="textoRespuesta") String contenido, @RequestParam(value="archivos") String archivos, Principal principal) throws InterruptedException {
	public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestPart(value="adjunto") MultipartFile multipartFile, @RequestParam(value="idSolicitud") int id, @RequestParam(value="accion") String accion, @RequestParam(value="textoRespuesta") String contenido, Principal principal) throws InterruptedException {
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
					query = "select date_format(f_sistema, '%Y/%m') from cdg where n_id_cdg = ?";
					ubicacionArchivo = this.jdbcTemplate.queryForObject(query, String.class, id);
					nombreArchivoOriginal = id + "_cdg_respuesta_" + multipartFile.getOriginalFilename();
				}
				
				query = "select s_estado from cdg where n_id_cdg = ?";
				String estadoSolicitud = this.jdbcTemplate.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("P")) {
					try {
						if(!multipartFile.isEmpty()) {
							
							@SuppressWarnings("unchecked")
							Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
							String server = ftp.getIp();
					        int port = ftp.getPuerto();
					        String user = ftp.getUsuario();
					        String pass = ftp.getClave();
					        
							/*byte[] bytes = multipartFile.getBytes();
							String cargo = "SIASEMP_CARGO_" + id + ".pdf";
				            Path path = Paths.get(UPLOADED_FOLDER + cargo);
				            Files.write(path, bytes);
				            
				            String codDigitalizacion = leerDesdePDF(UPLOADED_FOLDER + cargo, 1, "Cod.", "lizacion").trim();*/
				            
				            /*if(!codDigitalizacion.equals("ERROR")) {
				            	ObjectMapper mapper = new ObjectMapper();
					            List<Adjunto> adjuntos = mapper.readValue(archivos, new TypeReference<List<Adjunto>>(){});
					            List<InputStream> inputPdfList = new ArrayList<InputStream>();
					            inputPdfList.add(new FileInputStream(UPLOADED_FOLDER + cargo));
					            
					            ftpClient.connect(server, port);
					            ftpClient.login(user, pass);
					            ftpClient.enterLocalPassiveMode();
					            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					            
					            String hora = "";
					            for (Adjunto adjunto : adjuntos) {
					            	query = "select c.s_carpeta_pdf as ubicacion, a.s_file_pdf as nombre, concat('-', date_format(c.f_sistema, '%d%m%Y-%H%i%s')) as temporal from cdg c inner join cdg_archivos a on a.n_id_cdg = c.n_id_cdg where a.n_id_cdg_archivos = ?";
					                @SuppressWarnings("unchecked")
					                Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), adjunto.getValue());
					                String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
					                String rutaLocal = "E:/temp_cdg/" + id + "_" + archivo.getNombre();
					                File downloadFile1 = new File("E:/temp_cdg/" + id + "_" + archivo.getNombre());
					                hora = archivo.getTemporal();
					                OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
					                boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
					                outputStream1.close();
					                inputPdfList.add(new FileInputStream(rutaLocal));
					            }
					            
					            OutputStream outputStream =  new FileOutputStream("E:\\temp_cdg\\" + codDigitalizacion + hora + ".pdf");
					            
					            mergePdfFiles(inputPdfList, outputStream);*/
					        	ftpClient.connect(server, port);
					            if (ftpClient.login(user, pass)) {
					            	ftpClient.enterLocalPassiveMode();
					            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					            	ftpClient.changeWorkingDirectory(ubicacionArchivo);
					                boolean result = ftpClient.storeFile(id+"_cdg_respuesta_"+multipartFile.getOriginalFilename(), multipartFile.getInputStream());
					                if(result) {
										query = "update cdg set s_estado = ?, s_respuesta = ?, s_file_pdf_respuesta = ?, s_carpeta_pdf_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_cdg = ?";
										this.jdbcTemplate.update(query, accion, contenido, nombreArchivoOriginal, ubicacionArchivo, ip, id);
										TimeUnit.SECONDS.sleep(1);
										map.put("Status", 200);
					                }
					                else {
					                	map.put("Status", 300);
					                }
					                ftpClient.logout();
					                ftpClient.disconnect();
					            	/*query = "select configuracion_ncpp.x_ruta from configuracion_ncpp where configuracion_ncpp.id='125' and configuracion_ncpp.c_sede = ? and configuracion_ncpp.l_activo='S'";
					        		String repo = this.jdbcTemplate2.queryForObject(query, String.class, sede);
					        		System.out.println(repo);
						            File destino = new File(repo + "\\" + codDigitalizacion + hora + ".pdf");
						            File origen = new File("E:\\temp_cdg\\" + codDigitalizacion + hora + ".pdf");
						            FileUtils.copyToDirectory(origen, destino);*/
					            	//map.put("Status", 200);
					            }
					            else
					            	map.put("Status", 300);
				            /*}
				            else {
				            	map.put("Status", 350);
				            }*/
			            }
						else{
							query = "update cdg set s_estado = ?, s_respuesta = ?, s_ip_pc_respuesta = ?, f_sistema_respuesta = now() where n_id_cdg = ?";
							this.jdbcTemplate.update(query, accion, contenido, ip, id);
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
	
	@RequestMapping(value="/elevarCdg", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> elevarCdg(@RequestBody ValueCriteria vc, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "update cdg set s_superior = 'S' where n_id_cdg = ?";
				this.jdbcTemplate.update(sql, vc.getId());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/reasignarCdg", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> reasignarCdg(@RequestBody Parametro param, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "update cdg set c_usuario_asignado = ? where n_id_cdg = ?";
				this.jdbcTemplate.update(sql, param.getValor(), param.getCodigo());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/guardarTurno", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> guardarTurno(@RequestBody Turno turno, Principal principal) {
		
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "";
				String ip = getClientIp();
				if(turno.getTipo().equals("D")){
					String[] fechas = turno.getFechas().split(",");
					int cantidad = fechas.length;
					for(int i = 0; i < cantidad; i++) {
						sql = "insert into cdg_programacion (c_sede, c_usuario, f_fecha, c_usuario_asigna, s_ip_pc, f_sistema) values (?,?,?,?,?,NOW())";
						this.jdbcTemplate.update(sql, turno.getSede(), turno.getUsuario(), fechas[i], principal.getName(), ip);
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
						sql = "insert into cdg_programacion (c_sede, c_usuario, f_fecha, c_usuario_asigna, s_ip_pc, f_sistema) values (?,?,?,?,?,NOW())";
						this.jdbcTemplate.update(sql, turno.getSede(), turno.getUsuario(), cadenaFecha, principal.getName(), ip);
						i++;
					}
				}
				TimeUnit.SECONDS.sleep(2);
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/eliminarTurno", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> eliminarTurno(@RequestBody ValueCriteria vc, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "select concat(id_programacion, '-', c_sede, '-', c_usuario, '-',f_fecha,'-',c_usuario_asigna) from cdg_programacion where id_programacion = ?";
				String evento = this.jdbcTemplate.queryForObject(sql, String.class, vc.getId());
				sql = "insert into log_cdg (c_usuario, evento, f_sistema, s_ip) values (?,?,NOW(),?)";
				this.jdbcTemplate.update(sql, principal.getName(), evento, getClientIp());
				sql = "delete from cdg_programacion where id_programacion = ?";
				this.jdbcTemplate.update(sql, vc.getId());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	public static String leerDesdePDF(String pdfPath, int numPagina, String inicioIdent, String finIdent) {
        String cadenaFinal = "";
        try {
            PDDocument document = PDDocument.load(new File(pdfPath));
            document.getClass();        
            if (!document.isEncrypted()) {
                PDFTextStripperByArea area = new PDFTextStripperByArea();
                area.setSortByPosition(true);
                PDFTextStripper sArea = new PDFTextStripper();
                sArea.setStartPage(numPagina);
                sArea.setEndPage(numPagina);
                String pdfFileInText = sArea.getText(document);
                String cadenaInicio = inicioIdent;
                String cadenaFin = finIdent;
                int inicioPos = pdfFileInText.indexOf(cadenaInicio);
                int finPos = pdfFileInText.indexOf(cadenaFin);
                cadenaFinal = pdfFileInText.substring(inicioPos, finPos+36);
                cadenaFinal = cadenaFinal.substring(20).trim();
                document.close();
            }
        } 
        catch (Exception e) {
              cadenaFinal = "ERROR";
        }
        return cadenaFinal.trim();
    }
	
	public int enviarRepositorio(String sede, int id, String archivos, String cargo, String archivoFinal) throws Exception {
		String sql = "select s_ip as ftp, s_user as usuario, s_password as clave, concat('21') AS puerto from cdg_ftp_repositorio where c_sede = ?";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(sql, new FtpRowMapper(), sede); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        int resultado = 0;
        
        int union = unirAdjuntos(archivos, id, cargo, archivoFinal);
    	if(union == 200) {
            if(ftpCdgService.conectarFTP(sede)) {
	            if(ftpCdgService.cargarArchivo(archivoFinal)) {
	            	resultado = 200;
	            }
	            else {
	            	resultado = 250;
	            }
	        }
	        else
	        	resultado = 290;
	    	}
		else {
			resultado = 270;
    	}
    		

        
        /*FTPClient ftpClient = new FTPClient();
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
        */
		
		return resultado;
	}
	
	public int unirAdjuntos(String archivos, int id, String cargo, String archivoFinal) {
		
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
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
	        	String sql = "select c.s_carpeta_pdf as ubicacion, a.s_file_pdf as nombre, concat('-', date_format(c.f_sistema, '%d%m%Y-%H%i%s')) as temporal from cdg c inner join cdg_archivos a on a.n_id_cdg = c.n_id_cdg where a.n_id_cdg_archivos = ?";
	            @SuppressWarnings("unchecked")
	            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(sql, new ArchivoRowMapper(), adjunto.getValue());
	            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	            String rutaLocal = UPLOADED_FOLDER + archivo.getNombre();
	            File downloadFile1 = new File(UPLOADED_FOLDER + archivo.getNombre());

            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            	boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            	outputStream1.close();

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
	
	static void mergePdfFiles(List<InputStream> inputPdfList, OutputStream outputStream) throws Exception{
		
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
}
