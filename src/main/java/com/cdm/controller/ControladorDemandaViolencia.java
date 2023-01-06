package com.cdm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
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

import com.cdm.config.Conexion;
import com.cdm.entities.Archivo;
import com.cdm.entities.Ftp;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

@Controller
public class ControladorDemandaViolencia {
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    JdbcTemplate jdbcTemplate2 = new JdbcTemplate(con.ConectarMySQLAlimentos());
    
    final String queryFTPViolencia = "SELECT funcFTP(12) AS ftp, funcFTP(13) AS usuario, funcFTP(14) AS clave, concat('21') AS puerto";
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }  
    
    private static String UPLOADED_FOLDER = "E:/temp_cdg/";
    
	@GetMapping("/VIOLENCIAFAMILIAR")
	public String alimentos(Principal principal, Model model, String sede) {
		
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
		
        sql = "SELECT date_format(vf.f_sistema, '%d-%m-%y %r') as fechaIngreso, vf.*, vv.* FROM violencia_familiar vf \r\n" + 
        		"INNER JOIN victimas_violencia vv ON vv.n_id_violencia = vf.n_id_violencia where vf.s_estado IN ('P', 'T') GROUP BY vf.n_id_violencia";
        List<?> demandas =  this.jdbcTemplate2.queryForList(sql);

        model.addAttribute("demandas", demandas);
        model.addAttribute("tipo", "DEMANDAS VIOLENCIA");
        model.addAttribute("formulario", "VIOLENCIAFAMILIAR");
		return "vistas/listaViolencia";
	}
	
	@GetMapping("/buscarDemandaViolencia")
	public String buscarDocumentoFecha(Model model, String tipo, String texto, String inicio, String fin, Principal principal) {
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

			sql = "SELECT date_format(vf.f_sistema, '%d-%m-%y %r') as fechaIngreso, vf.*, vv.* FROM violencia_familiar vf \r\n"  + 
				"INNER JOIN victimas_violencia vv ON vv.n_id_violencia = vf.n_id_violencia where vf.x_expediente like ? or vv.s_nombres like ? GROUP BY vf.n_id_violencia";
			lista = this.jdbcTemplate2.queryForList(sql, "%" + texto + "%", "%" + texto + "%");
		}
		else if(tipo.equals("F")) {

			sql = "SELECT date_format(vf.f_sistema, '%d-%m-%y %r') as fechaIngreso, vf.*, vv.* FROM violencia_familiar vf \r\n"  + 
					"INNER JOIN victimas_violencia vv ON vv.n_id_violencia = vf.n_id_violencia where date(vf.s_sistema) >= ? and date(vf.s_sistema) <= ? GROUP BY vf.n_id_violencia";
			lista = this.jdbcTemplate2.queryForList(sql, inicio, fin );

		}
		
		model.addAttribute("demandas", lista);
		model.addAttribute("tipo", "BUSQUEDA " + inicio + " - " + fin);
		model.addAttribute("formulario", "VIOLENCIAFAMILIAR");
		return "vistas/listaViolencia";  
	}
	
	@GetMapping("/detalleViolencia")
	public String detalleAlimentos(Model model, String id, String formulario, Principal principal) {
		String query = "";
		
		query = "select s_estado from violencia_familiar where n_id_violencia = ?";
		String estado = this.jdbcTemplate2.queryForObject(query, String.class, id);
		
		if(estado.equals("P")) {
			query = "update violencia_familiar set s_estado = 'T', c_usuario_csjar = ? where n_id_violencia = ?";
			this.jdbcTemplate2.update(query, principal.getName(), id);
		}
		
		query = "SELECT date_format(vf.f_sistema, '%d-%m-%y %r') as fechaIngreso, date_format(vf.f_sistema_cdg, '%d-%m-%y %r') as fechaAtencion,  vv.*, vf.*, YEAR(NOW()) as anio FROM violencia_familiar vf  \r\n" + 
			"INNER JOIN victimas_violencia vv ON vv.n_id_violencia = vf.n_id_violencia where vf.n_id_violencia = ? GROUP BY vf.n_id_violencia";
		List<?> detalle = this.jdbcTemplate2.queryForList(query, id);
		query = " SELECT * FROM instancia WHERE instancia.c_sede = 0431";
		List<?> instancias = this.jdbcTemplate2.queryForList(query);
		String instancia = null;
		if(estado.equals("A")) {
			query = " SELECT i.x_nom_instancia FROM instancia i INNER JOIN violencia_familiar a on a.c_instancia = i.c_instancia WHERE n_id_violencia = ?";
			instancia = this.jdbcTemplate2.queryForObject(query, String.class, id);
		}
		model.addAttribute("instancias", instancias);
		model.addAttribute("instancia", instancia);
		model.addAttribute("detalle", detalle);
		model.addAttribute("formulario", formulario);
		model.addAttribute("sesion", principal.getName());

		return "vistas/detalleViolencia";
	}
	
	@RequestMapping(value="/verPDFViolencia/{campo}/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> verPDFViolencia(@PathVariable("campo") int campo, @PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPViolencia, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            if(campo == 1)
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_violencia, '_', date_format(f_sistema_cdg, '%Y%m%d_%H%i%s')) as temporal from violencia_familiar where n_id_violencia = ?";
            else
            	query = "select s_carpeta_pdf as ubicacion, s_file_medio_probatorio_pdf as nombre, concat(n_id_violencia, '_', date_format(f_sistema_cdg, '%Y%m%d_%H%i%s')) as temporal from violencia_familiar where n_id_violencia = ?";
            
            
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
	
	@RequestMapping(value="/enviarRespuestaVF", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaBanco(HttpServletResponse response, @RequestParam(value="idViolencia") int id, 
			@RequestParam(value="accion") String accion, @RequestPart(value="adjunto") MultipartFile multipartFile,
			@RequestParam(value="instancia") String instancia, @RequestParam(value="respuesta") String respuesta, 
			@RequestParam(value="expediente") String expediente, Principal principal) throws InterruptedException {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query = "select s_estado from violencia_familiar where n_id_violencia = ?";
				String estadoSolicitud = this.jdbcTemplate2.queryForObject(query, String.class, id);
				
				if(estadoSolicitud.equals("T")) {
					if(accion.equals("A")) {
						query = "select s_file_pdf from violencia_familiar where n_id_violencia = ?";
				        String archivoBD = this.jdbcTemplate2.queryForObject(query, String.class, id);
				        try {
				            int sendRepo = 0;
				            String archivoFinal = null;
				           
					        String sql = "select concat('-', date_format(NOW(), '%d%m%Y-%H%i%s'))";
					    	archivoFinal = this.jdbcTemplate2.queryForObject(sql, String.class);
					    	
					    	byte[] bytes = multipartFile.getBytes();
							String cargo = "PLATS_VF_" + id + ".pdf";
				            Path path = Paths.get(UPLOADED_FOLDER + cargo);
				            Files.write(path, bytes);
				            
					    	String codDigitalizacion = leerDesdePDF(UPLOADED_FOLDER + cargo, 1, "Cod.", "lizacion").trim();

			            	if(codDigitalizacion.equals("ERROR") || codDigitalizacion.length() != 25) {
			            		sendRepo = 350;
				            }
			            	else {
			            		archivoFinal = codDigitalizacion + archivoFinal + ".pdf";
					        	sendRepo = enviarRepositorio("0431", id, archivoBD, archivoFinal);
			            	}
			            	
				            if(sendRepo == 200) {
								query = "update violencia_familiar set s_estado = 'A', s_codigo = ?, x_expediente = ?, c_sede = '0431', c_instancia = ?, s_ip_pc = ?, x_observacion_ingreso = ?, f_sistema_cdg = NOW() where n_id_violencia= ?";
								this.jdbcTemplate2.update(query, archivoFinal, expediente, instancia, getClientIp(), respuesta.toUpperCase(), id);
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
					else {
						query = "update violencia_familiar set s_estado = 'R', s_ip_pc = ?, x_observacion_ingreso = ?, f_sistema_cdg = NOW() where n_id_violencia= ?";
						this.jdbcTemplate2.update(query, getClientIp(), respuesta.toUpperCase(), id);
						TimeUnit.SECONDS.sleep(1);
						map.put("Status", 200);
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
	
	public int enviarRepositorio(String sede, int id, String archivoBD, String archivoFinal) throws Exception {
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
        
        File downloadFile1 = new File(UPLOADED_FOLDER + archivoBD);
        
        List<InputStream> inputPdfList = new ArrayList<InputStream>();
        inputPdfList.add(new FileInputStream(UPLOADED_FOLDER + archivoBD));
        
        String medio = verificarMedioProbatorio(id);
        if(!medio.isEmpty())
        	inputPdfList.add(new FileInputStream(UPLOADED_FOLDER + medio));
        
        OutputStream outputStream =  new FileOutputStream(UPLOADED_FOLDER + archivoFinal);
        
        unirDocumentosPDF(inputPdfList, outputStream);
        
        if(!downloadFile1.exists()) {
        	resultado = 230;
        }
        else {
	        if (ftpClient.login(user, pass)) {
	        	ftpClient.enterLocalPassiveMode();
	        	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        	ftpClient.changeWorkingDirectory("/");
				
		        FileInputStream fis = new FileInputStream(UPLOADED_FOLDER + archivoFinal);
		        boolean result = ftpClient.storeFile(archivoFinal, fis);
		        if(result)
		            resultado = 200;
		        else 
		        	resultado = 250;
		        
		        ftpClient.logout();
		        ftpClient.disconnect();
	        }
	        else
	        	resultado = 290;
        }
		return resultado;
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
	
	public String verificarMedioProbatorio(int id) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate2.queryForObject(queryFTPViolencia, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        	
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        query = "select s_carpeta_pdf as ubicacion, s_file_medio_probatorio_pdf as nombre, concat(n_id_violencia, '_', date_format(f_sistema_cdg, '%Y%m%d_%H%i%s')) as temporal from violencia_familiar where n_id_violencia = ?";
        
        @SuppressWarnings("unchecked")
        Archivo archivo = (Archivo) this.jdbcTemplate2.queryForObject(query, new ArchivoRowMapper(), id);
        
        String remoteFile = archivo.getUbicacion() + "/" + archivo.getNombre();
        File downloadFile1 = new File(UPLOADED_FOLDER + archivo.getNombre());
        
        if(!downloadFile1.exists()) {
        	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream1);
            outputStream1.close();
            if (success)
            	System.out.println("DESCARGADO");
            else 
            	System.out.println("NO SE PUDO");
        } 
        
        return archivo.getNombre();
	}
	
	static void unirDocumentosPDF(List<InputStream> inputPdfList, OutputStream outputStream) throws Exception{
		
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
