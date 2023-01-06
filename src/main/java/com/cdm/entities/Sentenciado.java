package com.cdm.entities;

public class Sentenciado {
	private String tipo;
	private String dni;
	private String nombres;
	private String apellidos;
	private String nacimiento;
	private String celular;
	private String direccion;
	private String pais;
	private String expediente;
	private String sede;
	private String instancia;
	private String especialista;
	private String reglas;
	
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Sentenciado() {
		super();
	}
	public Sentenciado(String tipo, String dni, String nombres, String apellidos, String nacimiento, String direccion, String celular,
			String pais, String expediente, String sede, String instancia, String especialista, String reglas) {
		super();
		this.tipo = tipo;
		this.dni = dni;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.nacimiento = nacimiento;
		this.celular = celular;
		this.direccion = direccion;
		this.pais = pais;
		this.expediente = expediente;
		this.sede = sede;
		this.instancia = instancia;
		this.especialista = especialista;
		this.reglas = reglas;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getNacimiento() {
		return nacimiento;
	}
	public void setNacimiento(String nacimiento) {
		this.nacimiento = nacimiento;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getSede() {
		return sede;
	}
	public void setSede(String sede) {
		this.sede = sede;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getEspecialista() {
		return especialista;
	}
	public void setEspecialista(String especialista) {
		this.especialista = especialista;
	}
	public String getReglas() {
		return reglas;
	}
	public void setReglas(String reglas) {
		this.reglas = reglas;
	}
	
}
