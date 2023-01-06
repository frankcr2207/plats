package com.cdm.entities;

public class Expediente {
	private String id_correlativo;
	private String id_solicitud;
	private String numero;
	private String anio;
	private String cuaderno;
	private String id_instancia;
	private String instancia;
	private String nombre_parte;
	private String nombre_secretario;
	private String n_unico;
	private String x_formato;
	
	
	public String getId_correlativo() {
		return id_correlativo;
	}
	public void setId_correlativo(String id_correlativo) {
		this.id_correlativo = id_correlativo;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getN_unico() {
		return n_unico;
	}
	public void setN_unico(String n_unico) {
		this.n_unico = n_unico;
	}
	public String getX_formato() {
		return x_formato;
	}
	public void setX_formato(String x_formato) {
		this.x_formato = x_formato;
	}
	public String getId_instancia() {
		return id_instancia;
	}
	public void setId_instancia(String id_instancia) {
		this.id_instancia = id_instancia;
	}
	public String getId_solicitud() {
		return id_solicitud;
	}
	public void setId_solicitud(String id_solicitud) {
		this.id_solicitud = id_solicitud;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getCuaderno() {
		return cuaderno;
	}
	public void setCuaderno(String cuaderno) {
		this.cuaderno = cuaderno;
	}
	public String getNombre_parte() {
		return nombre_parte;
	}
	public void setNombre_parte(String nombre_parte) {
		this.nombre_parte = nombre_parte;
	}
	public String getNombre_secretario() {
		return nombre_secretario;
	}
	public void setNombre_secretario(String nombre_secretario) {
		this.nombre_secretario = nombre_secretario;
	}
	public Expediente(String correlativo, String id_solicitud, String numero, String anio, String cuaderno, String id_instancia, String instancia,
			String nombre_parte, String nombre_secretario, String n_unico, String x_formato) {
		super();
		this.id_correlativo = correlativo;
		this.id_solicitud = id_solicitud;
		this.numero = numero;
		this.anio = anio;
		this.cuaderno = cuaderno;
		this.id_instancia = id_instancia;
		this.instancia = instancia;
		this.nombre_parte = nombre_parte;
		this.nombre_secretario = nombre_secretario;
		this.n_unico = n_unico;
		this.x_formato = x_formato;
	}
	
	public Expediente() {
		
	}
}
