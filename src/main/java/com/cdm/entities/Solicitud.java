package com.cdm.entities;

public class Solicitud {
	private String id;
	private String fecha;
	private String correo;
	private String nombres;
	private String apellidos;
	private String documento;
	private String nacimiento;
	private String celular;
	private String discapacidad;
	private String instancia;
	private String cdm;
	private String expediente;
	private String parte;
	private String estado;
	
	public Solicitud() {
		
	}
	
	public Solicitud(String id, String fecha, String correo, String nombres, String apellidos, String documento,
			String nacimiento, String celular, String discapacidad, String instancia, String cdm, String expediente,
			String parte, String estado) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.correo = correo;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.documento = documento;
		this.nacimiento = nacimiento;
		this.celular = celular;
		this.discapacidad = discapacidad;
		this.instancia = instancia;
		this.cdm = cdm;
		this.expediente = expediente;
		this.parte = parte;
		this.estado = estado;
	}
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
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
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNacimiento() {
		return nacimiento;
	}
	public void setNacimiento(String nacimiento) {
		this.nacimiento = nacimiento;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getDiscapacidad() {
		return discapacidad;
	}
	public void setDiscapacidad(String discapacidad) {
		this.discapacidad = discapacidad;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getCdm() {
		return cdm;
	}
	public void setCdm(String cdm) {
		this.cdm = cdm;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getParte() {
		return parte;
	}
	public void setParte(String parte) {
		this.parte = parte;
	}
}
