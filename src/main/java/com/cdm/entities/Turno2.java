package com.cdm.entities;

public class Turno2 {
	private String sede;
	private String defensor;
	private String tipo;
	private String fechas;
	private String usuario;
	public Turno2(String sede, String defensor, String tipo, String fechas, String usuario) {
		super();
		this.sede = sede;
		this.usuario = usuario;
		this.tipo = tipo;
		this.fechas = fechas;
		this.defensor = defensor;
	}
	
	public String getDefensor() {
		return defensor;
	}

	public void setDefensor(String defensor) {
		this.defensor = defensor;
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
	public Turno2() {
		
	}
}
