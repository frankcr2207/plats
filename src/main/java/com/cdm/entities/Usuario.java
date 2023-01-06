package com.cdm.entities;

public class Usuario {
	private String dni;
	private String user;
	private String nombres;
	private String paterno;
	private String materno;
	private String nacimiento;
	private String perfil;
	private String estado;
	private String carga;
	private String correo;
	private String telefono;
	private	String especialidad;
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getPaterno() {
		return paterno;
	}
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}
	public String getMaterno() {
		return materno;
	}
	public void setMaterno(String materno) {
		this.materno = materno;
	}
	public String getNacimiento() {
		return nacimiento;
	}
	public void setNacimiento(String nacimiento) {
		this.nacimiento = nacimiento;
	}
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCarga() {
		return carga;
	}
	public void setCarga(String carga) {
		this.carga = carga;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
	public Usuario(String dni, String user, String nombres, String paterno, String materno, String nacimiento,
			String perfil, String estado, String carga, String correo, String telefono, String especialidad) {
		super();
		this.dni = dni;
		this.user = user;
		this.nombres = nombres;
		this.paterno = paterno;
		this.materno = materno;
		this.nacimiento = nacimiento;
		this.perfil = perfil;
		this.estado = estado;
		this.carga = carga;
		this.correo = correo;
		this.telefono = telefono;
		this.especialidad = especialidad;
	}
	public Usuario() {
		super();
	} 	
}
