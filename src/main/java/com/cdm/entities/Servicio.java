package com.cdm.entities;

public class Servicio {
	private long n_id;
	private String s_dni;
	private String s_nombres;
	private String s_apellidos;
	private String s_celular;
	private String s_correo;
	private String n_expediente;
	private String sede;
	private String instancia;
	private int n_id_tipo;
	private String s_observacion;
	private String s_respuesta;
	private String s_mensaje;
	private String atencion;
	private String s_estado;
	private String registro;
	public long getN_id() {
		return n_id;
	}
	public void setN_id(long n_id) {
		this.n_id = n_id;
	}
	public String getS_dni() {
		return s_dni;
	}
	public void setS_dni(String s_dni) {
		this.s_dni = s_dni;
	}
	public String getS_nombres() {
		return s_nombres;
	}
	public void setS_nombres(String s_nombres) {
		this.s_nombres = s_nombres;
	}
	public String getS_apellidos() {
		return s_apellidos;
	}
	public void setS_apellidos(String s_apellidos) {
		this.s_apellidos = s_apellidos;
	}
	public String getS_celular() {
		return s_celular;
	}
	public void setS_celular(String s_celular) {
		this.s_celular = s_celular;
	}
	public String getS_correo() {
		return s_correo;
	}
	public void setS_correo(String s_correo) {
		this.s_correo = s_correo;
	}
	public String getN_expediente() {
		return n_expediente;
	}
	public void setN_expediente(String n_expediente) {
		this.n_expediente = n_expediente;
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
	public int getN_id_tipo() {
		return n_id_tipo;
	}
	public void setN_id_tipo(int n_id_tipo) {
		this.n_id_tipo = n_id_tipo;
	}
	public String getS_observacion() {
		return s_observacion;
	}
	public void setS_observacion(String s_observacion) {
		this.s_observacion = s_observacion;
	}
	public String getS_respuesta() {
		return s_respuesta;
	}
	public void setS_respuesta(String s_respuesta) {
		this.s_respuesta = s_respuesta;
	}
	public String getS_mensaje() {
		return s_mensaje;
	}
	public void setS_mensaje(String s_mensaje) {
		this.s_mensaje = s_mensaje;
	}
	public String getAtencion() {
		return atencion;
	}
	public void setAtencion(String atencion) {
		this.atencion = atencion;
	}
	public String getS_estado() {
		return s_estado;
	}
	public void setS_estado(String s_estado) {
		this.s_estado = s_estado;
	}
	public String getRegistro() {
		return registro;
	}
	public void setRegistro(String registro) {
		this.registro = registro;
	}
	public Servicio(long n_id, String s_dni, String s_nombres, String s_apellidos, String s_celular, String s_correo,
			String n_expediente, String sede, String instancia, int n_id_tipo, String s_observacion, String s_respuesta,
			String s_mensaje, String atencion, String s_estado, String registro) {
		super();
		this.n_id = n_id;
		this.s_dni = s_dni;
		this.s_nombres = s_nombres;
		this.s_apellidos = s_apellidos;
		this.s_celular = s_celular;
		this.s_correo = s_correo;
		this.n_expediente = n_expediente;
		this.sede = sede;
		this.instancia = instancia;
		this.n_id_tipo = n_id_tipo;
		this.s_observacion = s_observacion;
		this.s_respuesta = s_respuesta;
		this.s_mensaje = s_mensaje;
		this.atencion = atencion;
		this.s_estado = s_estado;
		this.registro = registro;
	}
	public Servicio() {
		super();
	}
	
}
