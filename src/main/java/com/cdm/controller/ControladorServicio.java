package com.cdm.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.domain.vo.RequestActaVO;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.entities.Correo;
import com.cdm.entities.Servicio;
import com.cdm.service.external.ServicioExternalService;
import com.cdm.service.external.vo.ResponseResumenAsistenteVO;
import com.cdm.service1.SedeService;
import com.cdm.utils.Constantes;
import com.cdm.utils.ExcelArchivo;
import com.cdm.utils.ExportarExcel;
import com.cdm.utils.SmtpMailSender;

@Controller
public class ControladorServicio {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
    private SmtpMailSender smtpMailSender;
	
	private ServicioExternalService servicioExternalService;
	
	private SedeService sedeService;
	
	@Autowired
    private ExportarExcel jxlService;
    
	public ControladorServicio(ServicioExternalService servicioExternalService, SedeService sedeService) {
		this.servicioExternalService = servicioExternalService;
		this.sedeService = sedeService;
	}
	
    public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }  
	
	@GetMapping("/SERVICIO") 
	public String servicio(Model model, Principal principal) {
		try {
			List<?> lista = null;
			String sql = "SELECT c.*, DATE_FORMAT(c.f_registro, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(c.s_nombres, ' ', c.s_apellidos) AS nombresCompletos FROM servicio c \r\n" + 
					"WHERE c.s_estado = 'P'  ORDER BY c.f_registro asc";
			lista = this.jdbcTemplate.queryForList(sql);

			model.addAttribute("documentos", lista);
			model.addAttribute("tipo", "LISTA DE RECLAMOS, QUEJAS, ETC.");
			model.addAttribute("formulario", "SERVICIO");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaServicio";
	}
	
	@RequestMapping(value = "/buscarSolicitanteServicio", method = RequestMethod.GET)
	public String buscarCasillaFecha(Model model, @RequestParam(value="valor") String valor, Principal principal) {
		String sql;

		sql = "SELECT c.*, DATE_FORMAT(c.f_registro, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(c.s_nombres, ' ', c.s_apellidos) AS nombresCompletos FROM servicio c \r\n" + 
			"WHERE c.s_nombres like ? or c.s_apellidos like ? ORDER BY c.f_registro asc";
		List<?> lista = this.jdbcTemplate.queryForList(sql, "%" + valor + "%", "%" + valor + "%");
		
		model.addAttribute("documentos", lista);
		model.addAttribute("tipo", "BUSQUEDA - " + valor);
		model.addAttribute("formulario", "SERVICIO");
		return "vistas/listaServicio";  
	}
	
	@RequestMapping(value = "/detalleServicio", method = RequestMethod.GET)
	public String detalleServicio(Model model, @RequestParam(value="id") String id, Principal principal) {
		String query = "";

		query = "SELECT st.s_tipo, c.*, DATE_FORMAT(c.f_registro, '%d-%m-%Y %r') AS fechaRegistro, CONCAT(c.s_nombres, ' ', c.s_apellidos) AS nombresCompletos, s.s_sede, i.x_nom_instancia, DATE_FORMAT(c.f_atencion, '%d-%m-%Y %r') AS fechaAtencion\r\n" + 
				"FROM servicio c INNER JOIN servicio_tipo st ON st.n_id = c.n_id_tipo INNER JOIN sede s ON s.c_sede = c.c_sede INNER JOIN instancia i ON i.c_instancia = c.c_instancia WHERE c.n_id = ?";
		List<?> detalle = this.jdbcTemplate.queryForList(query, id);

		model.addAttribute("detalle", detalle);
		model.addAttribute("formulario", "SERVICIO");

		return "vistas/detalleServicio";
	}
	
	@RequestMapping(value="/enviarRespuestaServicio", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaServicio(HttpServletResponse response, @RequestParam(value="idSolicitud") int id, @RequestParam(value="correoRespuesta") String destino,  @RequestParam(value="textoRespuesta") String respuesta, @RequestParam(value="accion") String mensaje, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Status", 200);
		String ip = getClientIp();
		try {
			if(principal.getName() == null) {
				map.put("Status", 600);
			}
			else {
				String query;
				Correo correo = new Correo("Respuesta a su consulta y/o pedido.", destino, respuesta, null); 
				if(mensaje.equals("S")) {
					smtpMailSender.sendServicio(correo);
					query = "update servicio set s_respuesta = ?, s_mensaje = 'S', f_atencion = now(), s_estado = 'A' where n_id = ?";
				}
				else
					query = "update servicio set s_respuesta = ?, f_atencion = now(), s_estado = 'A' where n_id = ?";
				this.jdbcTemplate.update(query, respuesta, id);
			}
			TimeUnit.SECONDS.sleep(2);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@GetMapping("/reporteServicio") 
	public String reporteServicio(Model model, Principal principal) {
		return "vistas/reporteServicio";
	}
	
	//@GetMapping("/download/reporteServicio.xlsx")
	@RequestMapping(value="/download/reporteServicio.xlsx/{inicio}/{fin}", method=RequestMethod.GET)
    public void downloadCsv(HttpServletResponse response, @PathVariable("inicio") String inicio, @PathVariable("fin") String fin) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=ReporteServicioNCPP.xlsx");
        ByteArrayInputStream stream = ExcelArchivo.listaServicio(extraeDatos(inicio, fin));
        IOUtils.copy(stream, response.getOutputStream());
    }
	
	private List<Servicio> extraeDatos(String inicio, String fin){
    	String sql = "SELECT n_id, se.s_dni, se.s_nombres, se.s_apellidos, se.s_celular, se.s_correo, se.n_expediente, s.s_sede as sede, i.x_nom_instancia as instancia, se.s_observacion, date_format(se.f_registro, '%d-%m-%Y %r') as registro, date_format(se.f_atencion, '%d-%m-%Y %r') as atencion, se.s_estado, se.s_respuesta, se.s_mensaje, se.s_respuesta \r\n" + 
    		"FROM servicio se INNER JOIN sede s ON s.c_sede = se.c_sede INNER JOIN instancia i ON i.c_instancia = se.c_instancia \r\n" + 
    		"WHERE se.f_registro >= ? AND se.f_registro <= ? ORDER BY se.f_registro ASC";
    	System.out.println(inicio + " - " + fin);
    	return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Servicio>(Servicio.class), inicio, fin);
    }
	
	@GetMapping("/getFormConteoActasSij") 
	public String getFormConteoActasSij(Model model) {
		List<ResponseSedeVO> sedes = this.sedeService.getSedes();
		model.addAttribute("sedes", sedes);
		return "vistas/ncpp/actas";  
	}
	
	@GetMapping("/getConteoActasSij") 
	public @ResponseBody List<ResponseResumenAsistenteVO> getConteoActasSij(String sede, String fechaInicio, String fechaFin, boolean estado) {
		LocalDateTime fecIni = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		LocalDateTime fecFin = LocalDate.parse(fechaFin, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		long dias = ChronoUnit.DAYS.between(fecIni, fecFin);
		if(dias > 60) {
			throw new NoSuchElementException("El rango de fechas no debe superar los 60 dias");
		}
		return this.servicioExternalService.getConteoActasSij(sede, fechaInicio, fechaFin, estado);  
	}
	
	@GetMapping("/getActaSij") 
	public void getActaSij(HttpServletResponse response, @RequestParam(value="sede") String sede, @RequestParam(value="descargo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
		LocalDateTime descargo, @RequestParam(value="documento") String documento) throws MessagingException, IOException {
		if(this.servicioExternalService.getActaSij(sede, descargo, documento)) {
			File archivo = new File(Constantes.RUTA_SERVIDOR_LOCAL + documento.concat(Constantes.EXTENSION_WORD));
			response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=" + documento.concat(Constantes.EXTENSION_WORD));
	        InputStream stream = new BufferedInputStream(new FileInputStream(archivo));
	        IOUtils.copy(stream, response.getOutputStream());
		}
		else
			throw new NoSuchElementException("No se pudo descargar el documento");
	}
	
	@PostMapping("download/xlsx")
    public ResponseEntity<byte[]> conteoActas(@RequestParam String usuario, @RequestBody List<RequestActaVO> responseAudienciaActaVO) throws IOException {

        if(null!=responseAudienciaActaVO) {
            String filename = "api_data_"+ new Random().nextInt(100)+".xlsx";
            ByteArrayOutputStream  file = jxlService.downloadXls("api_data.xlsx", responseAudienciaActaVO, usuario);
            System.out.println(usuario);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file.toByteArray());
        }
        return null;
    
    }  
}
