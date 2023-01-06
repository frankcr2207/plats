package com.cdm.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.config.Conexion;
import com.cdm.entities.InstanciaCriteria;
import com.cdm.entities.Parametro;
import com.cdm.entities.Reasignacion;
import com.cdm.entities.Usuario;
import com.cdm.utils.WebServiceReniec;
import com.cdm.zmappers.UsuarioRowMapper;

@Controller
public class ControladorAdmin {
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
    
    public String nombreCompleto(Principal principal) {
        String sql = "select concat(s_nombres,' ',s_apellido_paterno,' ',s_apellido_materno) as nombres from usuarios where c_usuario = ?";
        String _nombre = this.jdbcTemplate.queryForObject(sql, String.class, principal.getName());
        return _nombre;
    }
    
    private static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }
    
	@GetMapping("/usuarios") 
	public String usuarios(Model model, Principal principal) {
		try {
			String query = "SELECT u.c_usuario, concat(u.s_nombres, ' ',u.s_apellido_paterno, ' ',u.s_apellido_materno) as nombresCompletos, p.s_perfil, u.idperfil, (SELECT COUNT(*) FROM r_usuario_cdm r WHERE r.c_usuario = u.c_usuario) AS conteoCDM,\r\n" + 
				"(SELECT COUNT(*) FROM sedeorganousuario s WHERE s.c_usuario = u.c_usuario and n_activo = 1) AS conteoCDG, u.s_activo from usuarios u INNER JOIN perfil p ON p.idperfil = u.idperfil ORDER BY p.s_orden ASC ";
			List<?> lista= this.jdbcTemplate.queryForList(query);
			query = "SELECT * FROM perfil order by s_orden asc";
			List<?> perfil = this.jdbcTemplate.queryForList(query);
			String sql = "SELECT c.n_id_cdm, c.s_cdm from cdm c";
			List<?> cdm = this.jdbcTemplate.queryForList(sql);
			sql = "SELECT * from sede";
			List<?> sedes = this.jdbcTemplate.queryForList(sql);
			model.addAttribute("sedesBD", sedes);
			model.addAttribute("tipo", "ADMINISTRACIÓN DE USUARIOS");
			model.addAttribute("usuarios",lista);
			model.addAttribute("perfil",perfil);
			model.addAttribute("cdmBD",cdm);
			model.addAttribute("formulario","usuarios");
		}
		catch(Exception ex) {
			
		}
		return "vistas/usuarios";  
	}
	
    
	@SuppressWarnings("unchecked")
	@GetMapping("/buscarUsuario")
	@ResponseBody
	public Usuario buscarUsuario(String id) {
		String sql = "select * from usuarios u where u.c_usuario = ?" ;
		Usuario usuario = (Usuario) this.jdbcTemplate.queryForObject(sql, new Object[] {id}, new UsuarioRowMapper());        
		return usuario;
	}
	
	@RequestMapping(value="/actualizarUsuario", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> actualizarUsuario(HttpServletResponse response, @RequestParam(value="usuario") String usuario, @RequestParam(value="perfil") String perfil, @RequestParam(value="especialidad") String especialidad, @RequestParam(value="estado") String estado, @RequestParam(value="carga") String carga, @RequestParam(value="correo") String correo, @RequestParam(value="telefono") String telefono, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String query = "update usuarios set c_especialidad = ?, s_activo = ?, idperfil = ?, n_carga = ?, s_correo = ?, s_telefono = ? where c_usuario = ?";
			this.jdbcTemplate.update(query, especialidad, estado, perfil, carga, correo, telefono, usuario);
			query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'E', 'USUARIOS', ?)";
			this.jdbcTemplate.update(query, principal.getName(), getClientIp(), especialidad + ", " + estado + ", "+ perfil + ", "+ carga + ", "+ correo + ", "+ telefono);
	        map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/restablecerClave", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> restablecerClave(HttpServletRequest response, @RequestParam(value="usuario") String usuario,  Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String query;
		try {
			String c_usuario = response.getParameter("usuario");
			query = "select u.idperfil from usuarios u WHERE u.c_usuario = ?";
			String perfilBD = this.jdbcTemplate.queryForObject(query, String.class, c_usuario);
			if(perfilBD.equals("2") || perfilBD.equals("8") || perfilBD.equals("9")) {
				query = "update usuarios set s_password = MD5(?) where c_usuario = ?";
				this.jdbcTemplate.update(query, "123456", c_usuario);
			}
			else {
				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
				query = "update usuarios set s_password = ? where c_usuario = ?";
				this.jdbcTemplate.update(query, bCryptPasswordEncoder.encode("123456"), c_usuario);
			}
			query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'E', 'USUARIOS', ?)";
			this.jdbcTemplate.update(query, principal.getName(), getClientIp(), "CLAVE RESTABLECIDA DE USUARIO " + c_usuario);
	       
	        map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/cambiarColor", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> cambiarColor(HttpServletRequest request, HttpServletResponse response,  Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String color = request.getParameter("color");
			String sql = "update usuarios set s_color = ? where c_usuario = ?";
			this.jdbcTemplate.update(sql, color, principal.getName());
			sql = "update agenda set c_color = ? where c_usuario = ?";
			this.jdbcTemplate.update(sql, color, principal.getName());
	        map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	/*@GetMapping("/buscarPersonaReniec")
	@ResponseBody
	public Usuario buscarPersonaReniec(String id) {
		Usuario usuario = new Usuario();

		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(id);
		if(datosReniec[0].equals("no encontrado")) {
			usuario.setDni("DNI ERROR");
			usuario.setNombres("DNI ERROR");
			usuario.setPaterno("DNI ERROR");
			usuario.setMaterno("DNI ERROR");
		}
		else {
			usuario.setDni(id);
			usuario.setNombres(datosReniec[0]);
			usuario.setPaterno(datosReniec[1]);
			usuario.setMaterno(datosReniec[2]);
		}
		return usuario;
	}*/
	
	@RequestMapping(value="/guardarNuevoUsuario", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> guardarNuevoUsuario(HttpServletResponse response, @RequestParam(value="dniUsuarioNuevo") String dni, @RequestParam(value="nombresUsuarioNuevo") String nombres, @RequestParam(value="paternoUsuarioNuevo") String paterno,  @RequestParam(value="maternoUsuarioNuevo") String materno, @RequestParam(value="c_usuarioNuevo") String usuario, @RequestParam(value="perfilUsuarioNuevo") String perfil, @RequestParam(value="estadoUsuarioNuevo") String estado,  @RequestParam(value="especialidadUsuarioNuevo") String especialidad, @RequestParam(value="correoUsuarioNuevo") String correo,  @RequestParam(value="telefonoUsuarioNuevo") String telefono, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String sql = "select c_usuario from usuarios where c_usuario = ?";
		String query;
		List<?> c_usuario = this.jdbcTemplate.queryForList(sql, String.class, usuario);
		try {
			if(c_usuario.isEmpty()) {
				if(perfil.equals("2") || perfil.equals("8") || perfil.equals("9")) {
					query = "insert into usuarios (c_usuario, s_nombres, s_apellido_paterno, s_apellido_materno, s_dni, s_password, s_activo, n_carga, idperfil, s_correo, s_telefono) values (?, ?, ?, ?, ?, MD5(?), ?, ?, ?, ?, ?)";
					if(perfil.equals("8"))
						this.jdbcTemplate.update(query, usuario.toUpperCase(), nombres, paterno, materno, dni, "123456", estado, "S", perfil, correo, telefono);
					else 
						this.jdbcTemplate.update(query, usuario.toUpperCase(), nombres, paterno, materno, dni, "123456", estado, "N", perfil, correo, telefono);
				}
				else {
					BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
					query = "insert into usuarios (c_usuario, s_nombres, s_apellido_paterno, s_apellido_materno, s_dni, s_password, s_activo, n_carga, idperfil, c_especialidad, s_correo, s_telefono) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					this.jdbcTemplate.update(query, usuario.toUpperCase(), nombres, paterno, materno, dni, bCryptPasswordEncoder.encode("123456"), estado, "S", perfil, especialidad, correo, telefono);
				}
				query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'I', 'USUARIOS', ?)";
				this.jdbcTemplate.update(query, principal.getName(), getClientIp(), usuario.toUpperCase());
		       
	        	map.put("Status", 200);
	        }
			else
				map.put("Status", 400);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/asignarInstancia", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> asignarInstancia(@RequestBody InstanciaCriteria ic, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String sql = "select c_usuario from r_usuario_instancia where c_usuario = ? AND c_instancia = ?";
		List<?> c_usuario = this.jdbcTemplate.queryForList(sql, ic.getUsuario(), ic.getInstancia());
		try {
			if(c_usuario.isEmpty()) {
				sql = "insert into r_usuario_instancia (c_usuario, c_instancia) values (?,?)";
				this.jdbcTemplate.update(sql, ic.getUsuario(), ic.getInstancia());
				sql = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'I', 'r_instancia_usuario', ?)";
				this.jdbcTemplate.update(sql, principal.getName(), getClientIp(), ic.getUsuario() + ", " + ic.getInstancia());
	        	map.put("Status", 200);
	        }
			else
				map.put("Status", 400);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/quitarInstancia", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> quitarInstancia(@RequestBody InstanciaCriteria ic, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String sql = "select c_usuario from r_usuario_instancia where c_usuario = ? AND c_instancia = ?";
		List<?> c_usuario = this.jdbcTemplate.queryForList(sql, ic.getUsuario(), ic.getInstancia());
		try {
			if(!c_usuario.isEmpty()) {
				sql = "delete from r_usuario_instancia where c_usuario = ? and c_instancia = ?";
				this.jdbcTemplate.update(sql, ic.getUsuario(), ic.getInstancia());
				sql = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'D', 'r_instancia_usuario', ?)";
				this.jdbcTemplate.update(sql, principal.getName(), getClientIp(), ic.getUsuario() + ", " + ic.getInstancia());
	        	map.put("Status", 200);
	        }
			else
				map.put("Status", 400);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
    
	@RequestMapping(value="/cambiarClave", method = RequestMethod.POST)
	public @ResponseBody void add(HttpServletRequest request, HttpServletResponse response, Principal principal) throws Exception {
		try {

			String clave = request.getParameter("clave");
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
			String sql = "update usuarios set s_password = ? where c_usuario = ?";
			this.jdbcTemplate.update(sql, bCryptPasswordEncoder.encode(clave), principal.getName());
			sql = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'D', 'usuarios', 'CAMBIO CLAVE MISMO USUARIO')";
			this.jdbcTemplate.update(sql, principal.getName(), getClientIp());
			
		}
		catch(Exception e) {
			
		}
	}
	
	@RequestMapping(value="/asignarCDM", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> asignarCDM(HttpServletRequest response, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String cdm = response.getParameter("cdm");
		String usuario = response.getParameter("usuario");

		try {
			String query = "select c_usuario from r_usuario_cdm where n_id_cdm = ? and c_usuario = ?";
			List<?> respuesta = this.jdbcTemplate.queryForList(query, String.class, cdm, usuario.toUpperCase());
			if(respuesta.isEmpty()) {
				query = "insert into r_usuario_cdm (n_id_cdm, c_usuario) values (?,?)";
				this.jdbcTemplate.update(query, cdm, usuario.toUpperCase());
				query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'I', 'r_instancia_cdm', ?)";
				this.jdbcTemplate.update(query, principal.getName(), getClientIp(), usuario + ", " + cdm);
				map.put("Status", 200);
			}
			else
				map.put("Status", 400);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/desasignarCDM", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> desasignarCDM(HttpServletRequest response, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String cdm = response.getParameter("cdm");
		String usuario = response.getParameter("usuario");

		try {
			String query = "delete from r_usuario_cdm where n_id_cdm = ? and c_usuario = ?";
			this.jdbcTemplate.update(query, cdm, usuario);
			query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'D', 'r_instancia_cdm', ?)";
			this.jdbcTemplate.update(query, principal.getName(), getClientIp(), usuario + ", " + cdm);
			map.put("Status", 200);

		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/asignarCDG", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> asignarCDG(HttpServletRequest response, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String sede = response.getParameter("sede");
		String usuario = response.getParameter("usuario");
		String organo = response.getParameter("organo");

		try {
			String query = "SELECT IF(COUNT(*) = 0, 2 , n_activo) AS estado from sedeorganousuario where c_sede = ? and n_id_organo = ? and c_usuario = ?";
			int estado = this.jdbcTemplate.queryForObject(query, int.class, sede, organo, usuario.toUpperCase());
			if(estado == 2) {
				query = "insert into sedeorganousuario (c_sede, c_usuario, n_id_organo, n_contador, n_activo) values (?,?,?,0,1)";
				this.jdbcTemplate.update(query, sede, usuario.toUpperCase(), organo);
				query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'I', 'sedeorganousuario', ?)";
				this.jdbcTemplate.update(query, principal.getName(), getClientIp(), sede + ", " + organo + ", " + usuario);
				map.put("Status", 200);
			}
			else if(estado == 1) {
				map.put("Status", 400);
			}
			else if(estado == 0){
				query = "update sedeorganousuario set n_activo = 1 where c_sede = ? and c_usuario = ? and n_id_organo = ?";
				this.jdbcTemplate.update(query, sede, usuario.toUpperCase(), organo);
				query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'U', 'sedeorganousuario', ?)";
				this.jdbcTemplate.update(query, principal.getName(), getClientIp(), sede + ", " + organo + ", " + usuario);
				map.put("Status", 200);
			}
				
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/desasignarCDG", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> desasignarCDG(HttpServletRequest response, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		String cdg = response.getParameter("cdg");
		String usuario = response.getParameter("usuario");

		try {
			String query = "update sedeorganousuario set n_activo = 0 where n_id_sou = ?";
			this.jdbcTemplate.update(query, cdg);
			query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'U', 'sedeorganousuario', ?)";
			this.jdbcTemplate.update(query, principal.getName(), getClientIp(), usuario + ", " + cdg);
			map.put("Status", 200);

		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}

	@GetMapping("/parametros") 
	public String parametros(Model model, Principal principal) {
		try {
			String _nombre = this.nombreCompleto(principal);
			String query = "select * from configuracion";
			List<?> lista= this.jdbcTemplate.queryForList(query);
			model.addAttribute("nombre",_nombre);
			model.addAttribute("tipo", "");
			model.addAttribute("parametros",lista);
			model.addAttribute("formulario","CONFIGURACIÓN DE PARÁMETROS");
		}
		catch(Exception ex) {
			
		}
		return "vistas/listaConf";  
	}
	
	@GetMapping("/liberacion") 
	public String liberacion(Model model, Principal principal) {
		try {
			String _nombre = this.nombreCompleto(principal);
			String query = "select *, date_format(f_fecha_liberacion, '%d-%m-%Y %r') as fechaLiberacion from liberacion";
			List<?> lista= this.jdbcTemplate.queryForList(query);
			model.addAttribute("nombre",_nombre);
			model.addAttribute("registros",lista);
			model.addAttribute("formulario","LIBERACIÓN DE SOLICITUDES");
		}
		catch(Exception ex) {
			
		}
		return "vistas/liberacion";  
	}
	
	@RequestMapping(value = "/verParametro", method = RequestMethod.GET)
	public String verParametro(Model model, @RequestParam(value="value") String cod, Principal principal) {
		String _nombre = this.nombreCompleto(principal);
		String query = "select * from configuracion where id = ?";
		List<?> lista= this.jdbcTemplate.queryForList(query, cod);
		model.addAttribute("nombre",_nombre);
		model.addAttribute("detalles", lista);
		model.addAttribute("formulario","CONFIGURACIÓN DE PARÁMETROS");
		return "vistas/detalleParametro";  
	}
	
	@RequestMapping(value="/actParam", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> actParam(@RequestBody Parametro parametro, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String query = "update configuracion set valor = ? where id = ?";
			this.jdbcTemplate.update(query, parametro.getValor(), parametro.getCodigo());
			query = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_nuevo) values (?, NOW(), ?, 'E', 'configuracion', ?)";
			this.jdbcTemplate.update(query, principal.getName(), getClientIp(), parametro.getValor() + ", " + parametro.getCodigo());
			map.put("Status", 200);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
	
	@RequestMapping(value="/reasignarSecretarios", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> reasignarSecretarios(@RequestBody Reasignacion rea, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "SELECT * FROM resolucion r INNER JOIN solicitudes_cdm s ON s.n_id = r.id_der_cdm WHERE r.c_usuario = ? AND s.s_estado = 'D'";
				List<?> carga = this.jdbcTemplate.queryForList(sql, rea.getOrigen());
				if(carga.isEmpty()) {
					map.put("Status", 250);
				}
				else {
					String ip = getClientIp();
					sql = "UPDATE resolucion r INNER JOIN solicitudes_cdm s ON s.n_id = r.id_der_cdm SET r.c_usuario = ? WHERE r.c_usuario = ? AND s.s_estado = 'D'";
					this.jdbcTemplate.update(sql, rea.getDestino(), rea.getOrigen());
					sql = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_anterior, c_valor_nuevo) values (?,NOW(),?,'E','RESOLUCION',?,?)";
					this.jdbcTemplate.update(sql, principal.getName(), ip, rea.getOrigen(), rea.getDestino());
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
	
	@RequestMapping(value="/reasignarAsistentes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,Object> reasignarAsistentes(@RequestBody Reasignacion rea, Principal principal) {
		Map<String,Object> map = new HashMap<String,Object>();

		try {
			if(principal.getName() == null) map.put("Status", 500); 
			else {
				String sql = "SELECT * FROM solicitudes_cdm s WHERE s.c_usuario = ? AND s.s_estado <> 'A'";
				List<?> carga = this.jdbcTemplate.queryForList(sql, rea.getOrigen());
				if(carga.isEmpty()) {
					map.put("Status", 250);
				}
				else {
					sql = "SELECT * FROM r_usuario_cdm r WHERE r.n_id_cdm = ? AND r.c_usuario = ?";
					List<?> cdm = this.jdbcTemplate.queryForList(sql, rea.getValor(), rea.getDestino());
					if(cdm.isEmpty()) 
						map.put("Status", 300);
					else {
						String ip = getClientIp();
						sql = "UPDATE solicitudes_cdm s SET s.c_usuario = ? WHERE s.c_usuario = ? AND s.s_estado <> 'A' AND s.n_id_cdm = ?";
						this.jdbcTemplate.update(sql, rea.getDestino(), rea.getOrigen(), rea.getValor());
						sql = "insert into auditoria (c_usuario, f_sistema, c_ip, c_evento, c_tabla, c_valor_anterior, c_valor_nuevo) values (?,NOW(),?,'E','SOLICITUDES_CDM',?,?)";
						this.jdbcTemplate.update(sql, principal.getName(), ip, rea.getOrigen(), rea.getDestino());
				        map.put("Status", 200);
					}
				}
		    }
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			map.put("Status", 500);
		}
		return map;
	}
}
