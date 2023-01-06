package com.cdm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.config.Conexion;
import com.cdm.entities.Correo;

@Component
public class SmtpMailSender {
	@Autowired
	JdbcTemplate jdbcTemplate;
    //final String directorio = "E:\\Apache Software Foundation\\Tomcat 9.0\\webapps\\SIGSOLPRUEBA\\WEB-INF\\classes\\static\\img\\logoCSJAR2.png";
    final String directorio = "E:\\Apache Software Foundation\\Tomcat 9.0\\webapps\\SIGSOL\\WEB-INF\\classes\\static\\img\\logoCSJAR2.png";
    //final String directorio = "D:\\Workspace Spring Tool Suite\\CDM\\src\\main\\resources\\static\\img\\logoCSJAR2.png";
	@Autowired
	private JavaMailSender javaMailSender;	
	
	public boolean send(String to, String subject, String body, MultipartFile archivo, String encuesta) {
		boolean flag = true;
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(subject);
			helper.setFrom(new InternetAddress("CORTE SUPERIOR DE JUSTICIA AREQUIPA - CDM Virtual <pj.csj.arequipa@gmail.com>"));
			helper.setTo(to);		
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + body + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Por favor NO RESPONDER a este mensaje. En caso de obtener una respuesta negativa por parte del CDM, ingrese una nueva solicitud con los datos requeridos en https://csjarequipa.pj.gob.pe/csjar/suga</i></strong><br><br></p>"
		                + "<br>" + encuesta + "<br><br>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			helper.addAttachment("respuesta.pdf", archivo);
			javaMailSender.send(message);
		}
		catch(MessagingException ex) {
			flag = false;
		}
		return flag;
	}
	
	
	public boolean sendFechas(String to, String subject, String body, String fecha1, String fecha2, MultipartFile archivo, String encuesta) {
		boolean flag = true;
		try {
			String sql = "select date_format(?, '%d-%m-%Y %r')";
			String fechaTexto1 = this.jdbcTemplate.queryForObject(sql, String.class, fecha1);
			String fechaTexto2 = this.jdbcTemplate.queryForObject(sql, String.class, fecha2);
	
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(subject);
			helper.setFrom(new InternetAddress("CORTE SUPERIOR DE JUSTICIA AREQUIPA - CDM Virtual <pj.csj.arequipa@gmail.com>"));
			helper.setTo(to);
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + body + "</div><br><br>"
		                + "<div>Primer día: " + fechaTexto1 + "</div><br>"
		                + "<div>Segundo día: " + fechaTexto2 + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Por favor NO RESPONDER a este mensaje. En caso de obtener una respuesta negativa por parte del CDM, ingrese una nueva solicitud con los datos requeridos en https://csjarequipa.pj.gob.pe/csjar/suga</i></strong><br><br></p>"
		                + "<br>" + encuesta + "<br><br>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			helper.addAttachment("respuesta.pdf", archivo);
			javaMailSender.send(message);
		}
		catch(MessagingException ex) {
			flag = false;
		}
		return flag;
	}
	
	public boolean sendSinArchivo(String to, String subject, String body, String encuesta) {
		boolean flag = true;
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(subject);
			helper.setFrom(new InternetAddress("CORTE SUPERIOR DE JUSTICIA AREQUIPA - CDM Virtual <pj.csj.arequipa@gmail.com>"));
			helper.setTo(to);		
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + body + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Por favor NO RESPONDER a este mensaje. En caso de obtener una respuesta negativa por parte del CDM, ingrese una nueva solicitud con los datos requeridos en https://csjarequipa.pj.gob.pe/csjar/suga</i></strong><br><br></p>"
		                + "<br>" + encuesta + "<br><br>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			javaMailSender.send(message);
		}
		catch(MessagingException ex) {
			flag = false;
		}
		return flag;
	}
	

	public boolean sendFechasSinArchivo(String to, String subject, String body, String fecha1, String fecha2, String encuesta){
		boolean flag = true;
		try {
			String sql = "select date_format(?, '%d-%m-%Y %r')";
			String fechaTexto1 = this.jdbcTemplate.queryForObject(sql, String.class, fecha1);
			String fechaTexto2 = this.jdbcTemplate.queryForObject(sql, String.class, fecha2);
	
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(subject);
			helper.setFrom(new InternetAddress("CORTE SUPERIOR DE JUSTICIA AREQUIPA - CDM Virtual <pj.csj.arequipa@gmail.com>"));
			helper.setTo(to);
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + body + "</div><br><br>"
		                + "<div>Primer día: " + fechaTexto1 + "</div><br>"
		                + "<div>Segundo día: " + fechaTexto2 + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Por favor NO RESPONDER a este mensaje. En caso de obtener una respuesta negativa por parte del CDM, ingrese una nueva solicitud con los datos requeridos en https://csjarequipa.pj.gob.pe/csjar/suga</i></strong><br><br></p>"
		                + "<br>" + encuesta + "<br><br>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			javaMailSender.send(message);
		}
		catch(MessagingException ex) {
			flag = false;
		}
		return flag;
	}
	
	public boolean sendServicio(Correo correo) throws IOException {
		boolean flag = true;
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(correo.getAsunto());
			helper.setFrom(new InternetAddress("CSJ AREQUIPA - Administración Módulo Penal Central <pj.csj.arequipa@gmail.com>"));
			helper.setTo(correo.getDestino());		
			helper.setText("<html><body><div><div>" + correo.getContenido() + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Sistema de atención al usuario, por favor no responda.</i></strong><br><br></p>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br></div><br><br>"
		              + "</div><br><br></body></html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			javaMailSender.send(message);
			flag = true;
		}
		catch(MessagingException ex) {
			flag = false;
			System.out.println(ex.getMessage());
		}
		return flag;
	}
	
	
	/*public void sendAbogado(String to, String subject, String body, String confirma, String dniAbogado, String password ) throws MessagingException {
		
		String adicional = "";
		if(confirma.equals("SI")) {
			adicional = "Su usuario es: "+ dniAbogado + "<br>Clave de acceso: " + password + "<br><br>";
		}
		else if(confirma.equals("NC")){
			adicional = "Su usuario es: "+ dniAbogado + "<br>Nueva clave de acceso: " + password + "<br><br>";
		}
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		
		helper = new MimeMessageHelper(message, true);
		helper.setSubject(subject);
		helper.setTo(to);		
		helper.setText("<html>"
			            + "<body>"
			             + "<div>"
			              + "<div>" + body + "</div><br><br>"
			                + "<div>" + adicional + "</div><br><br>"
			                + "<div><p style=\"font-size: 13px\"> <strong><i>CDM Virtual. Por favor NO RESPONDER a este mensaje. Ingrese una nueva solicitud en https://csjarequipa.pj.gob.pe/csjar/cdm.html</i></strong><br><br></p>"
			                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
			                + "</div><br><br>"
			              + "</div><br><br></body>"
			            + "</html>", true);
		helper.addInline("rightSideImage", new File(directorio));
		javaMailSender.send(message);

	}
	
	public void expedienteSolicitud(String to, String subject, String body, List<?> lista) {
		
		String expedientes = "";

		for(int i = 0; i < lista.size(); i++) {
			expedientes = expedientes + lista.get(i).toString().substring(13, lista.get(i).toString().length() - 1) + "<br>";
		}
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(subject);
			helper.setTo(to);		
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + body + "</div><br><br>"
		                + "<div>" + expedientes + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\" ><strong><i>CDM Virtual. Por favor NO RESPONDER a este mensaje. Ingrese una nueva solicitud en https://csjarequipa.pj.gob.pe/csjar/cdm.html</i></strong><br><br></p>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));  
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		} 
		
		javaMailSender.send(message);
	}*/
	
	public boolean sendNotificacion(Correo correo) throws IOException {
		boolean flag = true;
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			
			helper = new MimeMessageHelper(message, true);
			helper.setSubject(correo.getAsunto());
			helper.setFrom(new InternetAddress("CORTE SUPERIOR DE JUSTICIA AREQUIPA - Sistema de Casilla Fisica Judicial <pj.csj.arequipa@gmail.com>"));
			helper.setTo(correo.getDestino());		
			helper.setText("<html>"
		            + "<body>"
		             + "<div>"
		                + "<div>" + correo.getContenido() + "</div><br><br>"
		                + "<div><p style=\"font-size: 13px\"> <strong><i>Este es un mensaje automático, por favor no responda.</i></strong><br><br></p>"
		                + "<img src='cid:rightSideImage' style='float:left;width:243px;height:61px;'/><br>"
		                + "</div><br><br>"
		              + "</div><br><br></body>"
		            + "</html>", true);
			helper.addInline("rightSideImage", new File(directorio));
			
			if(!correo.getArchivo().isEmpty()) {
			    String nombre = nombreArchivo();
		        FileOutputStream fos = new FileOutputStream("E://temp/" + nombre);
		        byte[] decoder = Base64.getDecoder().decode(correo.getArchivo());
		        fos.write(decoder);
			    File multipartFile = new File("E://temp/" + nombre);
		        InputStream inputStreams = new FileInputStream(multipartFile);
		        MultipartFile fileUbicacion = new MockMultipartFile(multipartFile.getName(), inputStreams);
				helper.addAttachment(nombre, fileUbicacion);
			}
			javaMailSender.send(message);
			flag = true;
		}
		catch(MessagingException ex) {
			flag = false;
			System.out.println(ex.getMessage());
		}
		return flag;
	}
	
	public String nombreArchivo() {

		char [] chars = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
		int charsLength = chars.length;
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<10;i++){
		   buffer.append(chars[random.nextInt(charsLength)]);
		}

		return "ARCHIVO_" + buffer.toString() + ".pdf";
	}
}
