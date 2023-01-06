package com.cdm.entities;

public class Turno {
	private String sede;
	private String usuario;
	private String tipo;
	private String fechas;
	public Turno(String sede, String usuario, String tipo, String fechas) {
		super();
		this.sede = sede;
		this.usuario = usuario;
		this.tipo = tipo;
		this.fechas = fechas;
	}
	public String getSede() {
		return sede;
	}
	public void setSede(String sede) {
		this.sede = sede;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getFechas() {
		return fechas;
	}
	public void setFechas(String fechas) {
		this.fechas = fechas;
	}
	public Turno() {
		
	}
}
