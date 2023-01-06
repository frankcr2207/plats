package com.cdm.entities;

public class Archivo {
	private String nombre;
	private String ubicacion;
	private String temporal;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getTemporal() {
		return temporal;
	}
	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}
	public Archivo(String nombre, String ubicacion, String temporal) {
		super();
		this.nombre = nombre;
		this.ubicacion = ubicacion;
		this.temporal = temporal;
	}
	public Archivo() {
		
	}
	
}
