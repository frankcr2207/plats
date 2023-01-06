package com.cdm.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableScheduling
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select c_usuario as principal, s_password as credentails, true from usuarios where c_usuario = ? and s_activo = 'S'")
		.authoritiesByUsernameQuery("select u.c_usuario as principal, p.s_perfil as role from perfil p inner join usuarios u on u.idperfil = p.idperfil where c_usuario = ?")
		.passwordEncoder(passwordEncoder()).rolePrefix("ROLE_");  	
		
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// TODO Auto-generated method stub
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
		
				.antMatchers("/", "/js/**", "/css/**", "/webjars/**","/send_mail_view","/expedientes","/directorio","/guardarNuevoRegistro", "/obtInstancias", "/WebServiceReniec/{dni}/{cod}/{fecha}").permitAll()
				//.antMatchers("/profile","/espera","/cambiar","/reporte").hasAnyRole("USER,ADMIN")
				.antMatchers("/listaCopias").hasRole("SECRETARIO-CDM")
				.antMatchers("/audiencias").hasRole("COORDINADOR-NCPP")
				.antMatchers("/DEVOLUCION","/EXPEDICION","/COPIAS","/ENDOSE","/LECTURA","/DOCUMENTAL").hasRole("ASISTENTE-CDM/CDG")
				.antMatchers("/reporteGeneralCDM","/buscar").hasRole("ADMINISTRADOR-CDM")
				.antMatchers("/usuarios","/nuevoUsuario","/listaConf").hasRole("ADMINISTRADOR-SISTEMA")
				.antMatchers("/abogados","/solicitudExpedientes").hasRole("CONTROL-NCPP")
				.antMatchers("/sentenciados").hasAnyRole("CONTROL-REGLAS-NCPP","CONSULTA-REGLAS-NCPP","SECRETARIO-CDM")
				//.antMatchers(HttpMethod.GET,"/WebServiceReniec/{dni}/{cod}/{fecha}").hasIpAddress("192.168.1.8/csjar/cdm/logica/validarReniec.php")
				
				//.antMatchers("/devolucion","/expedicion","/copias","/endose","/lectura").hasAnyRole("ADMINISTRADOR","ASISTENTE-CDM")
				//.anyRequest().authenticated().and().httpBasic()
				.and()
				.formLogin()
				.loginPage("/")
				.permitAll()
				.defaultSuccessUrl("/principal",true)
				.and()
				.logout()
				.logoutSuccessUrl("/");
		
		//http.sessionManagement().maximumSessions(1).expiredUrl("/500");
		//http.sessionManagement().invalidSessionUrl("/500.html");
	}
	
	//public void configure(WebSecurity web) {
	//	web.ignoring().antMatchers("/directorio");
	//}

}

