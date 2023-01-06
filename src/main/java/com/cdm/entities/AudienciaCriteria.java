package com.cdm.entities;

public class AudienciaCriteria {
	private String numero;
	private String anio;
	private String cuaderno;
	private String sede;
	private String instancia;
	private String fecha;
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
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getSede() {
		return sede;
	}
	public void setSede(String sede) {
		this.sede = sede;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public AudienciaCriteria(String numero, String anio, String cuaderno, String sede, String instancia, String fecha) {
		super();
		this.numero = numero;
		this.anio = anio;
		this.cuaderno = cuaderno;
		this.sede = sede;
		this.instancia = instancia;
		this.fecha = fecha;
	}
	
}
