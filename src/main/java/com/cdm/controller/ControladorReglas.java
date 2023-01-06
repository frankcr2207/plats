package com.cdm.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.config.Conexion;
import com.cdm.entities.Archivo;
import com.cdm.entities.Ftp;
import com.cdm.entities.Persona;
import com.cdm.entities.Sentenciado;
import com.cdm.utils.WebServiceReniec;
import com.cdm.utils.WebServiceReniecJSON;
import com.cdm.zmappers.ArchivoRowMapper;
import com.cdm.zmappers.FtpRowMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class ControladorReglas {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
    final String queryFTP = "SELECT funcFTP(8) AS ftp, funcFTP(9) AS usuario, funcFTP(10) AS clave, concat('21') AS puerto";
    
    public String funcFoto(int val) throws FileNotFoundException, IOException {
        String cadena, ruta;
        //FileReader f = new FileReader("D:\\Workspace Spring Tool Suite\\CDM\\src\\main\\resources\\static\\file\\image.txt");
        if(val == 1)
        	ruta = "E:\\Apache Software Foundation\\Tomcat 9.0\\webapps\\SIGSOL\\WEB-INF\\classes\\static\\file\\noreniec.txt";
        else
        	ruta = "E:\\Apache Software Foundation\\Tomcat 9.0\\webapps\\SIGSOL\\WEB-INF\\classes\\static\\file\\image.txt";
        FileReader f = new FileReader(ruta);
        
        BufferedReader b = new BufferedReader(f);
        cadena = b.readLine();
        b.close();
        return cadena;
    }
    
	public String nombreCompleto(Principal principal) {
        String user = principal.getName();
        String sql = "select concat(s_nombres) as nombres from usuarios where c_usuario = ?";
        String _nombre = this.jdbcTemplate.queryForObject(sql, String.class, user);
        return _nombre;
    }
	
	@GetMapping("/nuevosSentenciados") 
	public String nuevosSentenciados(Model model, Principal principal) {
		String sql = "SELECT se.id_sentenciado, CONCAT(d.s_abreviacion, ': ',se.s_documento) as documento, CONCAT(se.s_nombres, ' ',se.s_apellidos) AS nombresCompletos, (SELECT COUNT(*) FROM rc_expediente ex WHERE ex.id_sentenciado = se.id_sentenciado) as procesos, \r\n" + 
				"DATE_FORMAT(se.f_registro, '%d-%m-%Y %r') AS fechaRegistro, se.s_estado, se.c_usuario_temp FROM rc_sentenciados se INNER JOIN rc_documento d ON d.n_id = se.s_tipo_documento WHERE (SELECT COUNT(*) FROM rc_reglas r \r\n" + 
				"INNER JOIN rc_expediente e ON e.id_expediente = r.id_expediente INNER JOIN rc_sentenciados s ON s.id_sentenciado = e.id_sentenciado\r\n" + 
				"WHERE s.id_sentenciado = se.id_sentenciado) = 0";
		List<?> lista = this.jdbcTemplate.queryForList(sql);
		model.addAttribute("sentenciados", lista);
		model.addAttribute("formulario", 1);
		return "vistas/sentenciados";
	}
	
	@RequestMapping(value = "/sentenciados", method = RequestMethod.GET)
	public String sentenciados(Model model, Principal principal, @RequestParam(value="id") String id, @RequestParam(value="idSen") int idSen) throws SocketException, IOException {
	//public String sentenciados(Model model, Principal principal, @RequestParam(value="id") String id, @RequestParam(value="form") String form, @RequestParam(value="idSen") int idSen) throws SocketException, IOException {
		String sql = "";
		List<?> lista = null;
		
		if(idSen != 0) {
			sql = "update rc_sentenciados set c_usuario_temp = NULL, s_estado = 'L' where id_sentenciado = ? and c_usuario_temp = ?";
			this.jdbcTemplate.update(sql, idSen, principal.getName());
		}
		
		/*if(form.equals("N")) {
			sql = "SELECT se.id_sentenciado, CONCAT(d.s_abreviacion, ': ',se.s_documento) as documento, CONCAT(se.s_nombres, ' ',se.s_apellidos) AS nombresCompletos, (SELECT COUNT(*) FROM rc_expediente ex WHERE ex.id_sentenciado = se.id_sentenciado) as procesos,\r\n" + 
					"DATE_FORMAT(se.f_registro, '%d-%m-%Y %r') AS fechaRegistro, se.s_estado FROM rc_sentenciados se INNER JOIN rc_documento d ON d.n_id = se.s_tipo_documento\r\n" + 
					"INNER JOIN rc_expediente ex2 ON ex2.id_sentenciado = se.id_sentenciado \r\n" + 
					"WHERE (SELECT COUNT(*) FROM rc_reglas r INNER JOIN rc_expediente e ON e.id_expediente = r.id_expediente INNER JOIN rc_sentenciados s ON s.id_sentenciado = e.id_sentenciado\r\n" + 
					"WHERE s.id_sentenciado = se.id_sentenciado) = 0 AND ex2.c_sede = ?";
			lista = this.jdbcTemplate.queryForList(sql, id);
			model.addAttribute("form", form);
		}
		else if(form.equals("G")) {
			sql = "SELECT r.id_sentenciado, CONCAT(d.s_abreviacion, ': ', r.s_documento) as documento, CONCAT(r.s_nombres, ' ',r.s_apellidos) AS nombresCompletos, (SELECT COUNT(*) FROM rc_expediente e WHERE e.id_sentenciado = r.id_sentenciado) as procesos, DATE_FORMAT(r.f_registro, '%d-%m-%Y %r') AS fechaRegistro, r.s_estado\r\n" + 
					"FROM rc_sentenciados r INNER JOIN rc_documento d ON d.n_id = r.s_tipo_documento INNER JOIN rc_expediente e ON e.id_sentenciado = r.id_sentenciado WHERE e.c_sede = ?";
			lista = this.jdbcTemplate.queryForList(sql, id);
			model.addAttribute("form", form);
		}
		else if(form.equals("B")) {*/
			sql = "SELECT r.id_sentenciado, CONCAT(d.s_abreviacion, ': ', r.s_documento) as documento, CONCAT(r.s_nombres, ' ',r.s_apellidos) AS nombresCompletos, (SELECT COUNT(*) FROM rc_expediente e WHERE e.id_sentenciado = r.id_sentenciado) as procesos, DATE_FORMAT(r.f_registro, '%d-%m-%Y %r') AS fechaRegistro, r.s_estado\r\n" + 
					", r.c_usuario_temp FROM rc_sentenciados r INNER JOIN rc_documento d ON d.n_id = r.s_tipo_documento \r\n" + 
					"INNER JOIN rc_expediente e ON e.id_sentenciado = r.id_sentenciado WHERE r.s_documento LIKE ? OR r.s_nombres LIKE ? OR r.s_apellidos LIKE ? GROUP BY r.s_documento";
			lista = this.jdbcTemplate.queryForList(sql, "%" + id + "%", "%" + id + "%", "%" + id + "%");
			model.addAttribute("form", 1);
		//}
        
		model.addAttribute("sentenciados", lista);
		model.addAttribute("sede", id);
		return "vistas/sentenciados";
	}
	
	@GetMapping("/pendientesReglas") 
	public String pendientesReglas(Model model, Principal principal) {
		String sql = "SELECT r.id_sentenciado, CONCAT(d.s_abreviacion, ': ', r.s_documento) as documento, CONCAT(r.s_nombres, ' ',r.s_apellidos) AS nombresCompletos, (SELECT COUNT(*) FROM rc_expediente e WHERE e.id_sentenciado = r.id_sentenciado) as procesos, DATE_FORMAT(r.f_registro, '%d-%m-%Y %r') AS fechaRegistro, r.s_estado\r\n" + 
				"FROM rc_sentenciados r INNER JOIN rc_documento d ON d.n_id = r.s_tipo_documento WHERE r.c_usuario_temp = ?";
		List<?> lista = this.jdbcTemplate.queryForList(sql, principal.getName());
		model.addAttribute("sentenciados", lista);
		return "vistas/sentenciados";
	}
	
	@GetMapping("/nuevoProcesado") 
	public String nuevoProcesado(Model model, Principal principal) {
		String sql = "select * from sede";
		List<?> sedes = this.jdbcTemplate.queryForList(sql);
		sql = "select * from rc_pais where id_pais <> 141";
		List<?> paises = this.jdbcTemplate.queryForList(sql);
		sql = "select id_secretario, concat(s_apellido_paterno, ' ', s_apellido_materno, ' ', s_nombres) as nombresCompletos from rc_secretario order by s_apellido_paterno asc";
		List<?> secretarios = this.jdbcTemplate.queryForList(sql);
		model.addAttribute("sedes", sedes);
		model.addAttribute("paises", paises);
		model.addAttribute("secretarios", secretarios);
		return "vistas/nuevoProcesado";
	}
	
	@RequestMapping(value = "/detalleSentenciado", method = RequestMethod.GET)
	public String mostrarDetalleSentenciado(Model model, @RequestParam(value="id") String id, Principal principal) throws FileNotFoundException, IOException {
		String query = "select idperfil from usuarios where c_usuario = ?";
		int perfil = this.jdbcTemplate.queryForObject(query, int.class, principal.getName());
		query = "select s_estado from rc_sentenciados where id_sentenciado = ?";
		String estado = this.jdbcTemplate.queryForObject(query, String.class, id);

		if(perfil == 11 && estado.equals("L")) {
			query = "update rc_sentenciados set c_usuario_temp = ?, s_estado = 'O' where id_sentenciado = ?";
			this.jdbcTemplate.update(query, principal.getName(), id);
		}
		
		query = "SELECT *, concat(r.s_nombres, ' ', r.s_apellidos) as nombresCompletos, DATE_FORMAT(r.f_nacimiento, '%d-%m-%Y') AS fechaNacimiento, DATE_FORMAT(r.f_registro, '%d-%m-%Y %r') AS fechaRegistro FROM rc_sentenciados r INNER JOIN rc_pais p ON p.id_pais = r.id_pais WHERE r.id_sentenciado = ?";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);
		query = "SELECT r.s_documento, r.id_pais FROM rc_sentenciados r WHERE r.id_sentenciado = ?";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, id);
		String dni = null;
		int pais = 0;
        for (Map<?, ?> row : rows) {
            dni = ((String) row.get("s_documento"));
            pais = ((int) row.get("id_pais"));
        }
        String image;
        if(pais == 141) {
        	Persona persona = new Persona();
        	WebServiceReniecJSON json = new WebServiceReniecJSON();
        	persona = json.getResponseWSReniec(dni);
        	try {
        		image = "data:image/png;base64," + persona.getFoto();
        	}
        	catch(Exception ex) {
        		image = "data:image/png;base64," + funcFoto(1);
        	}
        }
        else {
        	image = "data:image/png;base64," + funcFoto(0);
        }
		
		model.addAttribute("sentenciado", detalle);
		model.addAttribute("form", 1);
		model.addAttribute("image", image);
		model.addAttribute("perfil", perfil);
		model.addAttribute("sede", 1);
		model.addAttribute("sesion", principal.getName());
		return "vistas/detalleSentenciado2";
	}
	
	@RequestMapping(value="/guardarNuevoExpSen", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> guardarNuevoExpSen(HttpServletResponse response, @RequestParam(value="id") String id, @RequestParam(value="expediente") String expediente, @RequestParam(value="sede") String sede,  @RequestParam(value="instancia") String instancia, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "insert into rc_expediente (id_sentenciado, n_expediente, c_sede, c_instancia, f_registro) values (?,?,?,?,NOW())";
				this.jdbcTemplate.update(sql, id, expediente, sede, instancia);
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/modificarExpSen", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> modificarExpSen(HttpServletResponse response, @RequestParam(value="txtIdExpSen") String idExpediente, @RequestParam(value="posExpediente") String expediente, @RequestParam(value="posSede") String sede, @RequestParam(value="posInstancia") String instancia, @RequestParam(value="posSecretario") String secretario, @RequestParam(value="posReparacion") String reparacion,  @RequestParam(value="posMedidas") String medidas,  @RequestParam(value="posEstado") String estado, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql;
				if(estado.equals("B") || estado.equals("X")) {
					sql = "SELECT COUNT(*) FROM rc_reglas r WHERE r.id_expediente = ? AND r.s_estado = 'P'";
					int cantidad = this.jdbcTemplate.queryForObject(sql, int.class, idExpediente);
					if(cantidad != 0)
						map.put("Status", 300);
					else {
						sql = "update rc_expediente set n_expediente = ?, c_sede = ?, c_instancia = ?, id_secretario = ?, n_reparacion = ?, s_estado = ?, s_medidas = ? WHERE id_expediente = ?";
						this.jdbcTemplate.update(sql, expediente, sede, instancia, secretario, reparacion, estado, medidas, idExpediente);
			        	map.put("Status", 200);
					}
				}
				else {
					sql = "update rc_expediente set n_expediente = ?, c_sede = ?, c_instancia = ?, id_secretario = ?, n_reparacion = ?, s_estado = ?, s_medidas = ? WHERE id_expediente = ?";
					this.jdbcTemplate.update(sql, expediente, sede, instancia, secretario, reparacion, estado, medidas, idExpediente);
		        	map.put("Status", 200);
				}
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@SuppressWarnings("restriction")
	@RequestMapping(value="/guardarReglaSentenciado", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> guardarReglaSentenciado(HttpServletResponse response, @RequestPart(value="ubicacion") String ubicacion, @RequestParam(value="repara") float reparacion, @RequestParam(value="idRegla") int idRegla, @RequestParam(value="observacion") String observacion, Principal principal) throws SocketException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "select date_format(NOW(), 'reglas/%Y/%m')";
		        String directorio = this.jdbcTemplate.queryForObject(sql, String.class);
		        String nombreImagen="";
		        if(ubicacion.length() > 10) {
		        	FTPClient ftpClient = new FTPClient();
					@SuppressWarnings("unchecked")
					Ftp ftp = (Ftp) this.jdbcTemplate.queryForObject(queryFTP, new FtpRowMapper());
					String server = ftp.getIp();
			        int port = ftp.getPuerto();
			        String user = ftp.getUsuario();
			        String pass = ftp.getClave();
			        ftpClient.connect(server, port);
			        if (ftpClient.login(user, pass)) {
			        	int flag = (ftpClient.cwd(directorio));
			        	if(flag != 250) {
			        		ftpClient.makeDirectory(directorio);
			        	}
			        	ftpClient.enterLocalPassiveMode();
		            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		            	ftpClient.changeWorkingDirectory(directorio);
		            	
		            	ubicacion = ubicacion.substring(15, ubicacion.length());
		            	ubicacion = ubicacion.substring(0, ubicacion.length() - 19);
		            	
		            	String[] partes = ubicacion.split(",");
		            	
		            	BufferedImage image = null;
		            	byte[] imageByte;
		            	BASE64Decoder decoder = new BASE64Decoder();
		            	imageByte = decoder.decodeBuffer(partes[1]);
		            	ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		            	image = ImageIO.read(bis);
		            	bis.close();
		            	sql = "SELECT CONCAT(r.n_id, '_ubicacion_', s.s_apellidos, '.png') FROM rc_sentenciados s INNER JOIN rc_expediente e ON s.id_sentenciado = e.id_sentenciado\r\n" + 
		            			"INNER JOIN rc_reglas r ON r.id_expediente = e.id_expediente WHERE r.n_id = ?";
				        String strUrl = this.jdbcTemplate.queryForObject(sql, String.class, idRegla);
				        
		            	File outputfile = new File("E://temp/" + strUrl);
		            	ImageIO.write(image, "png", outputfile);
		            	
		            	File multipartFile = new File("E://temp/" + strUrl);
		            	InputStream inputStreams = new FileInputStream(multipartFile);
		            	MultipartFile fileUbicacion = new MockMultipartFile(multipartFile.getName(), inputStreams);
		            	nombreImagen = strUrl;
		                boolean result = ftpClient.storeFile(nombreImagen, fileUbicacion.getInputStream());
		                if(result) {
		                	sql = "update rc_reglas set n_reparacion = ?, c_usuario_regla = ?, s_ruta_archivo = ?, s_nombre_archivo = ?, f_regla = now(), s_estado = 'E', s_observaciones = ? where n_id = ?";
		    				this.jdbcTemplate.update(sql, reparacion, principal.getName(), directorio, nombreImagen, observacion.trim().toUpperCase(), idRegla);
		    		        map.put("Status", 200);
		                }
		                else
		                	map.put("Status", 300);
			        }
		        }
		        else{
		        	sql = "update rc_reglas set n_reparacion = ?, c_usuario_regla = ?, s_ruta_archivo = ?, s_nombre_archivo = ?, f_regla = now(), s_estado = 'E', s_observaciones = ? where n_id = ?";
    				this.jdbcTemplate.update(sql, reparacion, principal.getName(), directorio, nombreImagen, observacion.trim().toUpperCase(), idRegla);
    		        map.put("Status", 200);
		        }
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/guardarNuevaCita", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> guardarNuevaCita(HttpServletResponse response, @RequestParam(value="id") String id, @RequestParam(value="cita") String cita, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql;
				sql = "insert into rc_reglas (id_expediente, c_usuario, f_registro, f_cita) VALUES (?,?,NOW(),?)";
				this.jdbcTemplate.update(sql, id, principal.getName(), cita);
				sql = "SELECT CONCAT(s.s_nombres, ' ', s.s_apellidos, ' - EXP: ', e.n_expediente) FROM rc_expediente e INNER JOIN rc_sentenciados s ON s.id_sentenciado = e.id_sentenciado\r\n" + 
						"WHERE e.id_expediente = ?";
				String descripcion = this.jdbcTemplate.queryForObject(sql, String.class, id);
				sql = "insert into rc_agenda (id_expediente, c_usuario, f_evento_inicio, f_evento_fin, s_descripcion, s_color) VALUES (?,?,?,DATE_ADD(?, INTERVAL 15 MINUTE),?,'#087A00')";
				this.jdbcTemplate.update(sql, id, principal.getName(), cita, cita, descripcion);
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/liberarReglas", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> liberarReglas(Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "update rc_sentenciados set c_usuario_temp = NULL, s_estado = 'L' where c_usuario_temp = ?";
				this.jdbcTemplate.update(sql, principal.getName());
		        map.put("Status", 200);
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/guardarNuevoProcesado", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> nuevoProcesado(Principal principal, @RequestBody Sentenciado sentenciado) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "select count(*) from rc_sentenciados where s_documento = ?";
				int filas = this.jdbcTemplate.queryForObject(sql, int.class, "00" + sentenciado.getDni());
				if(filas == 0) {
					GeneratedKeyHolder holder = new GeneratedKeyHolder();
					jdbcTemplate.update(new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							if(!sentenciado.getTipo().equals("1"))
								sentenciado.setDni("00" + sentenciado.getDni());
							
							PreparedStatement statement = con.prepareStatement("insert into rc_sentenciados (s_tipo_documento, s_documento, s_nombres, s_apellidos, f_nacimiento, id_pais, n_celular, s_domicilio, f_registro, s_estado, c_usuario) VALUES (?,?,?,?,?,?,?,?,now(),'L',?) ", Statement.RETURN_GENERATED_KEYS);
					        statement.setString(1, sentenciado.getTipo());
					        statement.setString(2, sentenciado.getDni());
					        statement.setString(3, sentenciado.getNombres().toUpperCase());
					        statement.setString(4, sentenciado.getApellidos().toUpperCase());
					        statement.setString(5, sentenciado.getNacimiento());
					        statement.setString(6, sentenciado.getPais());
					        statement.setString(7, sentenciado.getCelular());
					        statement.setString(8, sentenciado.getDireccion().toUpperCase());
					        statement.setString(9, principal.getName());
					        return statement;
						}
					}, holder);
					long primaryKey = holder.getKey().longValue();
					sql = "insert into rc_expediente (id_sentenciado, n_expediente, c_sede, c_instancia, id_secretario, n_reparacion, s_medidas, s_estado, f_registro) values (?,?,?,?,?,0,?,'T',NOW())";
					this.jdbcTemplate.update(sql, primaryKey, sentenciado.getExpediente(), sentenciado.getSede(), sentenciado.getInstancia(), sentenciado.getEspecialista(), sentenciado.getReglas().toUpperCase());
					map.put("Status", 200);
				}
				else {
					map.put("Status", 300);
				}
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@GetMapping("/verAgendaReglas") 
	public String verAgenda(Model model, Principal principal) {
		return "vistas/verAgendaReglas";  
	}
	
	@RequestMapping(value="/verArchivoReglas/{id}", method=RequestMethod.GET)
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

            query = "select s_ruta_archivo as ubicacion, s_nombre_archivo as nombre, concat(n_id, '_', date_format(f_registro, '%Y%m%d_%H%i%s')) as temporal from rc_reglas where n_id = ?";
            @SuppressWarnings("unchecked")
            Archivo archivo = (Archivo) this.jdbcTemplate.queryForObject(query, new ArchivoRowMapper(), id);
            
            String verifica = archivo.getNombre().substring(archivo.getNombre().length() - 4, archivo.getNombre().length());

            String remoteFile1 = archivo.getUbicacion() + "/" + archivo.getNombre();
	
            File downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();
 
            if (success) {
                System.out.println("Archivo convertido correctamente.");
            }
            else {
            	System.out.println("No se pudo.");
            }
            
            if(!verifica.equals(".pdf")) {
            	try {
            	Document doc = new Document(PageSize.A4.rotate(), 36, 36, 20, 20);
                PdfWriter.getInstance(doc, new FileOutputStream("E:/temp/" + archivo.getTemporal() + "_reglas_convertido.pdf"));
                doc.open();
                Image imagen = Image.getInstance("E:/temp/" + archivo.getTemporal() + "_" + archivo.getNombre());
                imagen.scaleToFit(PageSize.A4.getWidth()+100, PageSize.A4.getHeight()+100);
                imagen.setAlignment(Element.ALIGN_CENTER);
                doc.add(imagen);
                doc.close();
                downloadFile1 = new File("E:/temp/" + archivo.getTemporal() + "_reglas_convertido.pdf");
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
	
	@GetMapping("/reporteReglas") 
	public String reporteReglas(Model model, Principal principal) {
		return "vistas/reporteReglas";
	}
	
	@RequestMapping(value = "/detReporteReglas", method = RequestMethod.GET)
	public String detReporteReglas(Model model, @RequestParam(value="inicio") String inicio, @RequestParam(value="fin") String fin, Principal principal) {
		String sql = "SELECT s.id_sentenciado, d.s_abreviacion, s.s_documento, CONCAT(s.s_nombres, ' ', s.s_apellidos) as nombresCompletos, p.s_pais, s.n_celular,\r\n" + 
				"e.n_expediente, se.s_sede, i.x_nom_instancia, case e.s_estado when 'T' then 'TRAMITE' when 'O' then 'OBSERVADO' when 'B' then 'BIOMETRICO' when 'N' then 'NO CORRESPONDE' when 'P' then 'PENA VENCIDA'\r\n" + 
				"end as estado, coalesce(date_format(r.f_cita,'%d-%m-%Y %r'),'') as cita, coalesce(date_format(r.f_regla, '%d-%m-%Y %r'),'') as regla, coalesce( r.s_observaciones,'') as obs,\r\n" + 
				"case r.s_estado when 'E' then 'ENTREVISTADO' when 'P' then 'PENDIENTE' end as estado2 FROM rc_sentenciados s INNER JOIN rc_expediente e ON e.id_sentenciado = s.id_sentenciado INNER JOIN rc_reglas r ON r.id_expediente = e.id_expediente\r\n" + 
				"INNER JOIN sede se ON se.c_sede = e.c_sede INNER JOIN instancia i ON i.c_instancia = e.c_instancia INNER JOIN rc_secretario sec ON sec.id_secretario = e.id_secretario INNER JOIN rc_documento d ON d.n_id = s.s_tipo_documento\r\n" + 
				"INNER JOIN rc_pais p ON p.id_pais = s.id_pais WHERE ((date(r.f_regla) >= ? AND date(r.f_regla) <= ? AND r.c_usuario_regla = ?) OR (DATE(r.f_registro) >= ? AND DATE(r.f_registro) <= ? AND r.c_usuario = ?))";
		List<?> lista = this.jdbcTemplate.queryForList(sql, inicio, fin, principal.getName(), inicio, fin, principal.getName());
		
		model.addAttribute("reglas", lista);

		return "vistas/detReporteReglas";  
	}
}
