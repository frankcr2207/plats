package com.cdm.entities;

public class Persona {
	private String dni;
	private String nombres;
	private String paterno;
	private String materno;
	private String padre;
	private String madre;
	private String nacimiento;
	private String sexo;
	private String nivel;
	private String estado;
	private String lugar_nacimiento;
	private String caducidad;
	private String codigo;
	private String foto;
	private String firma;
	
	public Persona(String dni, String nombres, String paterno, String materno, String padre, String madre,
			String nacimiento, String sexo, String nivel, String estado, String lugar_nacimiento, String caducidad,
			String codigo, String foto, String firma) {
		super();
		this.dni = dni;
		this.nombres = nombres;
		this.paterno = paterno;
		this.materno = materno;
		this.padre = padre;
		this.madre = madre;
		this.nacimiento = nacimiento;
		this.sexo = sexo;
		this.nivel = nivel;
		this.estado = estado;
		this.lugar_nacimiento = lugar_nacimiento;
		this.caducidad = caducidad;
		this.codigo = codigo;
		this.foto = foto;
		this.firma = firma;
	}
	
	public Persona() {
		super();
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

	public String getPadre() {
		return padre;
	}

	public void setPadre(String padre) {
		this.padre = padre;
	}

	public String getMadre() {
		return madre;
	}

	public void setMadre(String madre) {
		this.madre = madre;
	}

	public String getNacimiento() {
		return nacimiento;
	}

	public void setNacimiento(String nacimiento) {
		this.nacimiento = nacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getLugar_nacimiento() {
		return lugar_nacimiento;
	}

	public void setLugar_nacimiento(String lugar_nacimiento) {
		this.lugar_nacimiento = lugar_nacimiento;
	}

	public String getCaducidad() {
		return caducidad;
	}

	public void setCaducidad(String caducidad) {
		this.caducidad = caducidad;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}
}