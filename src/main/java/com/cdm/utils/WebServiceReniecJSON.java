package com.cdm.utils;

import java.security.Key;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cdm.config.Conexion;
import com.cdm.entities.Calculo;
import com.cdm.entities.Instancia;
import com.cdm.entities.Parametros;
import com.cdm.entities.Persona;
import com.cdm.entities.Usuario;
import com.cdm.entities.ValueCriteria;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin(origins="http://localhost")
public class WebServiceReniecJSON {
	/*private static String SECRET_KEY = "PoweredByJorgeFranklynCoaquiraRamos2207AndJavaAndSpringBoot";
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/WebServiceReniec/{dni}/{cod}/{fecha}", method=RequestMethod.GET)
	public Persona persona(@PathVariable(value="dni") String dni, @PathVariable(value="cod") String cod, @PathVariable(value="fecha") String fecha) throws ParseException, java.text.ParseException {
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(dni);
		String nombres ="";
		String paterno = "";
		String materno = "";
		String codigo = "";
		String fechaNac = "";
		try {
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");

			Date date = null;
			if(!datosReniec[0].equals("MBRE': 'no encontrado")) {
				fechaNac = datosReniec[5];
				date = parseador.parse(fechaNac);

			}
			
			if(datosReniec[0].equals("MBRE': 'no encontrado") || !formateador.format(date).equals(fecha) || !datosReniec[26].equals(cod)) {
				dni = "ERROR";
				nombres = "ERROR";
				paterno = "ERROR";
				materno = "ERROR";
			}
			else {
				nombres = datosReniec[0];		
				paterno = datosReniec[1];
				materno = datosReniec[2];
			}
		}
		catch (Exception ex) {
			dni = "XC";
			nombres = "XC";
			paterno = "XC";
			materno = "XC";
		}

    	return new Persona(dni, "", nombres, paterno, materno,"","","","");
    }*/
	
	@RequestMapping(value="/WebServiceReniec/{dni}/{cod}/{fecha}", method=RequestMethod.GET)
	public Map<String,Object> persona(@PathVariable(value="dni") String dni, @PathVariable(value="cod") String cod, @PathVariable(value="fecha") String fecha) throws ParseException, java.text.ParseException {
		Map<String,Object> map = new HashMap<String,Object>();
    	
		Persona persona = this.getResponseWSReniec(dni);
		Date date = null;
		SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");

		if(persona.getDni().equals("NN") || persona.getDni().equals("XX"))
			map.put("dni", "ERROR");
		else {
			date = parseador.parse(persona.getNacimiento());
			if(persona.getDni().equals(dni) && formateador.format(date).equals(fecha) && persona.getCodigo().equals(cod)) {
				map.put("nombres", persona.getNombres());
				map.put("paterno", persona.getPaterno());
				map.put("materno", persona.getMaterno());
			}
			else {
				map.put("dni", "ERROR");
			}
		}
		return map;
    }
	/*
	@RequestMapping(value="/WebServiceReniecDNI/{dni}", method=RequestMethod.GET)
	public Persona personaXDNI(@PathVariable(value="dni") String dni) throws ParseException, java.text.ParseException {
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(dni);
		String nombres ="";
		String paterno = "";
		String materno = "";
		String nacimiento = "";
		String edad = "";
		String sexo = "";
		try {
			if(datosReniec[0].equals("no encontrado")) {
				dni = "ERROR";
				nombres = "DNI NO EXISTE";
			}
			else {
				
				nombres = datosReniec[0];		
				paterno = datosReniec[1];
				materno = datosReniec[2];
				nacimiento = datosReniec[5];		
				edad = CalculaEdad(datosReniec[5]);
				sexo = datosReniec[6];
			}
		}
		catch (Exception ex){
			dni = "XC";
			nombres = "XC";
			paterno = "XC";
			materno = "XC";
		}
    	return new Persona(dni, "", nombres, paterno, materno, nacimiento, edad, sexo, "");
    }
	
	@RequestMapping(value="/WebServiceReniecCOD/{dni}", method=RequestMethod.GET)
	public Persona personaXDNI2(@PathVariable(value="dni") String dni) throws ParseException, java.text.ParseException {
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(dni);
		String nombres ="";
		String paterno = "";
		String materno = "";
		String nacimiento = "";
		String edad = "20";
		String sexo = "";
		String cod = "";
		if(datosReniec[0].equals("no encontrado")) {
			dni = "ERROR";
			nombres = "DNI NO EXISTE";
		}
		else {
			String manzana = "", lote = "";
			if(!datosReniec[23].equals(""))
				manzana = "MZ."+ datosReniec[23];
			if(!datosReniec[24].equals(""))
				lote = "LT."+ datosReniec[24]; 
			
			nombres = datosReniec[0];		
			paterno = datosReniec[1];
			materno = datosReniec[2];
			nacimiento = datosReniec[16] + " " + datosReniec[17] + " " + datosReniec[18] + " " + datosReniec[19] + " " + 
					datosReniec[20] + " " + datosReniec[21] + " " +datosReniec[22] + " " + manzana + " " + lote + ", " +
					datosReniec[11] + ", " +datosReniec[10] + ", " + datosReniec[9];
			sexo = datosReniec[6];
			cod = datosReniec[26];
		}

    	return new Persona(dni, "", nombres, paterno, materno, nacimiento.trim(), edad, sexo, cod);
    }
	
	private String CalculaEdad(String fecha) throws java.text.ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = null;
        fechaDate = formato.parse(fecha);
		String strDateFormat = "yyyy-MM-dd";  
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); 
		String query = "SELECT TIMESTAMPDIFF(YEAR,?,NOW())";
		String calculaEdad = jdbcTemplate.queryForObject(query, String.class, objSDF.format(fechaDate));
		return calculaEdad;
	}
	
	@RequestMapping(value = "/WSReniec", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Persona WSReniec(@RequestBody Persona person) {
		
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(person.getDni());
		Persona persona = new Persona();
		String fechaNac = "";
		try {
			
			if(datosReniec[0].equals("no encontrado") ) {
				persona.setDni("ERROR");
				persona.setNombres("ERROR");
				persona.setPaterno("ERROR");
				persona.setMaterno("ERROR");
				persona.setToken("ERROR");
			}
			else{
				persona.setDni(person.getDni());
				persona.setNombres(datosReniec[0]);
				persona.setPaterno(datosReniec[1]);
				persona.setMaterno(datosReniec[2]);
				persona.setToken(datosReniec[27]);
			}
			
		}
		catch (Exception ex) {
			persona.setDni("XC");
			persona.setNombres("XC");
			persona.setPaterno("XC");
			persona.setMaterno("XC");
			persona.setToken("XC");
		}

    	return persona;
    }
	
	@RequestMapping(value = "/WSToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Persona WSToken(@RequestBody Persona person) {
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(person.getDni());
		String fechaNac = "";
		Persona persona = new Persona();
		
		try {
			
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");

			Date date = null;
			if(!datosReniec[0].equals("MBRE': 'no encontrado")) {
				fechaNac = datosReniec[5];
				date = parseador.parse(fechaNac);
			}
			
			if(datosReniec[0].equals("MBRE': 'no encontrado") || !formateador.format(date).equals(person.getNacimiento()) || !datosReniec[26].equals(person.getCodigo())) {
				persona.setDni("ERROR");
				persona.setNombres("ERROR");
				persona.setPaterno("ERROR");
				persona.setMaterno("ERROR");
			}
			else{
				persona.setDni(person.getDni());
				persona.setNombres(datosReniec[0]);
				persona.setPaterno(datosReniec[1]);
				persona.setMaterno(datosReniec[2]);
				persona.setToken(generarJWT(person.getToken()));
			}
			
		}
		catch (Exception ex) {
			persona.setDni("XC");
			persona.setNombres("XC");
			persona.setPaterno("XC");
			persona.setMaterno("XC");
		}

    	return persona;
    }
	
	@RequestMapping(value = "/WSTokenNCPP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Persona WSTokenNCPP(@RequestBody Persona person) {
		String datosReniec[] = WebServiceReniec.obtenerDatosReniec(person.getDni());
		String fechaNac = "";
		int anio = 0;
		Persona persona = new Persona();
		
		try {
			
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formateador = new SimpleDateFormat("yyyy");

			Date date = null;
			if(!datosReniec[0].equals("MBRE': 'no encontrado")) {
				fechaNac = datosReniec[5];
				date = parseador.parse(fechaNac);
			}
			
			if(datosReniec[0].equals("MBRE': 'no encontrado") || !formateador.format(date).equals(person.getNacimiento()) || !datosReniec[26].equals(person.getCodigo())) {
				persona.setDni("ERROR");
				persona.setNombres("ERROR");
				persona.setPaterno("ERROR");
				persona.setMaterno("ERROR");
			}
			else{
				persona.setDni(person.getDni());
				persona.setNombres(datosReniec[0]);
				persona.setPaterno(datosReniec[1]);
				persona.setMaterno(datosReniec[2]);
				persona.setToken(generarJWT(person.getToken()));
			}
			
		}
		catch (Exception ex) {
			persona.setDni("XC");
			persona.setNombres("XC");
			persona.setPaterno("XC");
			persona.setMaterno("XC");
		}

    	return persona;
    }
	
	protected static SecureRandom random = new SecureRandom();
    
    public synchronized String generateToken(String username) {
            long longToken = Math.abs( random.nextLong() );
            String random = Long.toString( longToken, 16 );
            return random;
    }
    
    @RequestMapping(value = "/solicitarToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String solicitarToken(@RequestBody ValueCriteria vc) {
		String token = generarJWT(vc.getId());
		return token;
    }
    
    public static String generarJWT(String issuer) {
    	String id = "APP_SIJJUP";
    	String subject = "WEBSERVICECONSUME";
    	long ttlMillis = 1000;
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
      
        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }  
      
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
    
    public static Claims decodificarJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        System.out.println(claims);
        return claims;
    }*/
    
    public Persona getResponseWSReniec(String dni) {
		RestTemplate restTemplate = new RestTemplate();
		Persona persona = restTemplate.getForObject(
				"http://172.28.206.18:8080/ServiceReniec/getWsReniecOrigin?dni=" + dni, Persona.class);
		return persona;
	}
    
    @RequestMapping(value = "/WSToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object>  WSTokenNCPP(@RequestBody Parametros pr) throws ParseException, java.text.ParseException {
    	Map<String,Object> map = new HashMap<String,Object>();
    	
		Persona persona = this.getResponseWSReniec(pr.getParam1());
		SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formateador = new SimpleDateFormat("yyyy");
		
		Date date = null;
			
		if(persona.getDni().equals("NN") || persona.getDni().equals("XX"))
			map.put("dni", persona.getDni());
		else {
			date = parseador.parse(persona.getNacimiento());
			if(persona.getDni().equals(pr.getParam1()) && formateador.format(date).equals(pr.getParam2()) && persona.getCodigo().equals(pr.getParam3())) {
				map.put("dni", persona.getDni());
				map.put("nombres", persona.getNombres());
				map.put("paterno", persona.getPaterno());
				map.put("materno", persona.getMaterno());
				map.put("nacimiento", persona.getNacimiento());
			}
			else {
				map.put("dni", "SC");
			}
		}

    	return map;
    }
    
    @GetMapping("/buscarPersonaReniec")
	public Usuario buscarPersonaReniec(String id) {
		Usuario usuario = new Usuario();
		Persona persona = this.getResponseWSReniec(id);
		usuario.setDni(persona.getDni());
		usuario.setNombres(persona.getNombres());
		usuario.setPaterno(persona.getPaterno());
		usuario.setMaterno(persona.getMaterno());
		return usuario;
	}
    
    @RequestMapping(value="/WSTokenDNI/{dni}", method=RequestMethod.GET)
	public Map<String,Object> personaXDNI(@PathVariable(value="dni") String dni) throws ParseException, java.text.ParseException {
    	Map<String,Object> map = new HashMap<String,Object>();
    	Persona persona = this.getResponseWSReniec(dni);
		map.put("dni", persona.getDni());
    	map.put("nombres", persona.getNombres());
		map.put("paterno", persona.getPaterno());
		map.put("materno", persona.getMaterno());
    	return map;
    }
    
    @RequestMapping(value="/WSTokenSigsol/{dni}", method=RequestMethod.GET)
	public Map<String,Object> WSTokenSigsol(@PathVariable(value="dni") String dni) throws ParseException, java.text.ParseException {
    	Map<String,Object> map = new HashMap<String,Object>();
    	Persona persona = this.getResponseWSReniec(dni);
    	map.put("dni", persona.getDni());
    	map.put("nombres", persona.getNombres());
		map.put("paterno", persona.getPaterno());
		map.put("materno", persona.getMaterno());
		map.put("nacimiento", persona.getNacimiento());
		return map;
    }
    
    @RequestMapping(value = "/WSTokenSijjup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object>  WSTokenSijjup(@RequestBody Parametros pr) throws ParseException, java.text.ParseException {
		
    	Map<String,Object> map = new HashMap<String,Object>();
    	Persona persona = this.getResponseWSReniec(pr.getParam1());
    	map.put("dni", persona.getDni());
    	map.put("nombres", persona.getNombres());
		map.put("paterno", persona.getPaterno());
		map.put("materno", persona.getMaterno());
		map.put("foto", persona.getFoto());
    	return map;
    }
}
