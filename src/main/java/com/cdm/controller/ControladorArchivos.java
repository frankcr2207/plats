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

import javax.mail.MessagingException;
import javax.swing.DefaultListModel;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.config.Conexion;
import com.cdm.entities.Adjunto;
import com.cdm.entities.Archivo;
import com.cdm.entities.Ftp;
import com.cdm.entities.Secretario;
import com.cdm.entities.ValueCriteria;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class ControladorArchivos {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Conexion con = new Conexion();
    
    final String queryFTP = "SELECT funcFTP(8) AS ftp, funcFTP(9) AS usuario, funcFTP(10) AS clave, concat('21') AS puerto";
    final String queryFTPCDG = "SELECT funcFTP(14) AS ftp, funcFTP(15) AS usuario, funcFTP(16) AS clave, concat('21') AS puerto";
    final String queryFTPCJ = "SELECT funcFTP(29) AS ftp, funcFTP(30) AS usuario, funcFTP(31) AS clave, concat('21') AS puerto";
    
    static String DIR_SERVER_P = "E:/temp_cdg/";
    //static String DIR_SERVER_P = "E:/temp_cdg_prueba/";
	
	@RequestMapping(value="/verAdjunto/{id}", method=RequestMethod.GET)
	private ResponseEntity<Object> getPDF1(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
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

            query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id, '_', date_format(f_registro, '%Y%m%d_%H%i%s')) as temporal from solicitudes_cdm where n_id = " + id;
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper());
            
            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());

            String remoteFile1 = archivo.getUbicacion() + "/" + id + "_" + archivo.getNombre();
	
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
            
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4, 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()-36, PageSize.A4.getHeight()+20);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_convertido.pdf");
                }
            	catch (Exception ex) {
            		
            	}
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
	
	@RequestMapping(value="/verArancel/{id}", method=RequestMethod.GET)
	private ResponseEntity<Object> getArancel(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
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

            query = "select s_carpeta_arancel_pdf as ubicacion, s_file_arancel_pdf as nombre, concat(n_id, '_', date_format(f_registro, '%Y%m%d_%H%i%s')) as temporal from solicitudes_cdm where n_id = " + id;
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper());

            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());
            
            String remoteFile1 = archivo.getUbicacion() + "/" + id + "_" + archivo.getNombre();
            
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
            
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4, 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()-36, PageSize.A4.getHeight()+20);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_convertido.pdf");
                }
            	catch (Exception ex) {
            		
            	}
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
	
	@RequestMapping(value="/verRespuesta/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> getPDF3(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
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
            
            query = "select s_carpeta_respuesta_pdf as ubicacion, s_file_respuesta_pdf as nombre, concat(n_id, '_', date_format(f_registro, '%Y%m%d_%H%i%s')) as temporal from solicitudes_cdm where n_id = " + id;
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper());

            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());
            
            String adjunto = archivo.getUbicacion() + "/" + id + "_" + archivo.getNombre();
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
            	
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4, 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()-36, PageSize.A4.getHeight()+20);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_convertido.pdf");
                }
            	catch (Exception ex) {
            		
            	}
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
	
	@RequestMapping(value="/verAdjuntoAbogado/{dni}", method=RequestMethod.GET)
	public ResponseEntity<Object> getAdjuntoAbogado(@PathVariable("dni") String dni) throws FileNotFoundException, MessagingException {
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

            query = "select s_carpeta_adjunto_pdf as ubicacion, s_file_adjunto_pdf as nombre, concat(s_dni, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from abogados where s_dni = " + dni;
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper());

            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());
            
            String adjunto = archivo.getUbicacion() + "/" + dni + "_" + archivo.getNombre();
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
            	
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4, 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()-36, PageSize.A4.getHeight()+20);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_convertido.pdf");
                }
            	catch (Exception ex) {
            		
            	}
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
	
	@RequestMapping(value="/descargarPDFCDG2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> elevarCdg(@RequestBody ValueCriteria vc, Principal principal) {
		FTPClient ftpClient = new FTPClient();
		Map<String,Object> map = new HashMap<String,Object>();
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select c.s_carpeta_pdf as ubicacion, a.s_file_pdf as nombre, concat(c.n_id_cdg, '_', date_format(c.f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg c inner join cdg_archivos a on a.n_id_cdg = c.n_id_cdg where a.n_id_cdg_archivos = ?";
            
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), vc.getId());

            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	
            File downloadFile1 = new File(DIR_SERVER_P + archivo.getNombre());
    
            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
                outputStream1.close();
                if (success)
                	System.out.println("DESCARGADO");
                else 
                	System.out.println("NO SE PUDO");
            
            Path filePath = Paths.get(DIR_SERVER_P + archivo.getNombre());
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
	
	@RequestMapping(value="/verPDFCDG2/{archivo}", method=RequestMethod.GET)
	private ResponseEntity<Object> verPDFCDG2(@PathVariable("archivo") String archivo) throws FileNotFoundException, MessagingException {
		ResponseEntity<Object> responseEntity = null;
		
		File downloadFile1 = new File(DIR_SERVER_P + archivo);
		
		try {
		InputStreamResource resource = new InputStreamResource(new FileInputStream(DIR_SERVER_P + archivo));
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
	
	@RequestMapping(value="/verArchivoCompleto/{archivo}", method=RequestMethod.GET)
	private ResponseEntity<Object> verArchivoCompleto(@PathVariable("archivo") String archivo) throws FileNotFoundException, MessagingException {
		ResponseEntity<Object> responseEntity = null;
		
		File downloadFile1 = new File(DIR_SERVER_P + archivo);
		
		try {
		InputStreamResource resource = new InputStreamResource(new FileInputStream(DIR_SERVER_P + archivo));
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
	
	@RequestMapping(value="/verPDFCDG/{campo}/{id}", method=RequestMethod.GET)
	private ResponseEntity<Object> verPDFCDG(@PathVariable("campo") int campo, @PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
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
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat(n_id_cdg, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";
            else if(campo == 2) 
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_dos as nombre, concat(n_id_cdg, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";
            else if(campo == 3)
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_tres as nombre, concat(n_id_cdg, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";
            else if(campo == 4) 
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_cuatro as nombre, concat(n_id_cdg, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";
            else if(campo == 5)
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_cinco as nombre, concat(n_id_cdg, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";

            
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);

            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	
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
	
	@RequestMapping(value="/verRespuestaPdfCDG/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> getPfdRespuesta(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf_respuesta as ubicacion, s_file_pdf_respuesta as nombre, concat(n_id_cdg, '_', date_format(f_sistema_respuesta, '%Y%m%d_%H%i%s')) as temporal from cdg where n_id_cdg = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);
            
            String adjunto = archivo.getUbicacion() + "/" + archivo.getNombre();
            String remoteFile1 = adjunto;

            File downloadFile1 = new File(DIR_SERVER_P + archivo.getNombre());

            if (!downloadFile1.exists()) {
            	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
                outputStream1.close();
     
                if (success) {
                    System.out.println("Descargado.");
                }
                else {
                	System.out.println("No se pudo.");
                }
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
	
	@RequestMapping(value="/verPDFCJ/{campo}/{id}", method=RequestMethod.GET)
	private ResponseEntity<Object> verPDFCJ(@PathVariable("campo") int campo, @PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCJ, new FtpRowMapper()); 
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
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf as nombre, concat('cj_', n_id_casillajudicial, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";
            else if(campo == 2) 
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_dos as nombre, concat('cj_', n_id_casillajudicial, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";
            else if(campo == 3)
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_tres as nombre, concat('cj_', n_id_casillajudicial, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";
            else if(campo == 4) 
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_cuatro as nombre, concat('cj_', n_id_casillajudicial, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";
            else if(campo == 5)
            	query = "select s_carpeta_pdf as ubicacion, s_file_pdf_cinco as nombre, concat('cj_', n_id_casillajudicial, '_', date_format(f_sistema, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";

            
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);
            
            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());

            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	
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
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4, 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()-36, PageSize.A4.getHeight()+20);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_convertido.pdf");
                }
            	catch (Exception ex) {
            		
            	}
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
	
	@RequestMapping(value="/verRespuestaPdfCJ/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> verRespuestaPdfCJ(@PathVariable("id") String id) throws FileNotFoundException, MessagingException {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCJ, new FtpRowMapper()); 
		String server = ftp.getIp();
        int port = ftp.getPuerto();
        String user = ftp.getUsuario();
        String pass = ftp.getClave();
        try {
        	 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            query = "select s_carpeta_pdf_respuesta as ubicacion, s_file_pdf_respuesta as nombre, concat(n_id_casillajudicial, '_', date_format(f_sistema_respuesta, '%Y%m%d_%H%i%s')) as temporal from casillajudicial where n_id_casillajudicial = ?";
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
	
	@RequestMapping(value="/unirArchivos/{archivos}", method=RequestMethod.GET)
	public ResponseEntity<Object> unirArchivos(@PathVariable("archivos") String archivos) throws Exception {
		FTPClient ftpClient = new FTPClient();
		ResponseEntity<Object> responseEntity = null;
		String query = "";
		@SuppressWarnings("unchecked")
		Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTPCDG, new FtpRowMapper()); 
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
            	query = "select c.s_carpeta_pdf as ubicacion, a.s_file_pdf as nombre, concat(c.n_id_cdg, '_', date_format(c.f_sistema, '%Y%m%d_%H%i%s')) as temporal from cdg c inner join cdg_archivos a on a.n_id_cdg = c.n_id_cdg where a.n_id_cdg_archivos = ?";
                @SuppressWarnings("unchecked")
                Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), adjunto.getValue());
                archivoFinal = archivo.getTemporal();
                String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
                String rutaLocal = DIR_SERVER_P + archivo.getNombre();
                File downloadFile1 = new File(DIR_SERVER_P + archivo.getNombre());
                if (!downloadFile1.exists()) {
                	OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                    boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
                    outputStream1.close();
                }
                inputPdfList.add(new FileInputStream(rutaLocal));
            }
            
            archivoFinal = "SIASEMP_union_" + archivoFinal + ".pdf";
            OutputStream outputStream =  new FileOutputStream(DIR_SERVER_P + archivoFinal);
            File downloadFile = new File(DIR_SERVER_P + archivoFinal);
            
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
		
		/*
        Document document = new Document();
        List<PdfReader> readers = new ArrayList<PdfReader>();
        int totalPages = 0;

        Iterator<InputStream> pdfIterator = 
                inputPdfList.iterator();

        while (pdfIterator.hasNext()) {
                InputStream pdf = pdfIterator.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages = totalPages + pdfReader.getNumberOfPages();
        }

        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();

        PdfContentByte pageContentByte = writer.getDirectContent();

        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        Iterator<PdfReader> iteratorPDFReader = readers.iterator();
        while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();
                while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                      document.newPage();
                      pdfImportedPage = writer.getImportedPage(
                              pdfReader,currentPdfReaderPage);
                      pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                      currentPdfReaderPage++;
                }
                currentPdfReaderPage = 1;
        }
        outputStream.flush();
        document.close();
        outputStream.close();*/
    }
}
