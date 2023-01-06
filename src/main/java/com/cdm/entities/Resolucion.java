package com.cdm.entities;

public class Resolucion {
	private String id;
	private String numero;
	private String fecha;
	private String fallo;
	private String sumilla;
	private String observacion;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getFallo() {
		return fallo;
	}
	public void setFallo(String fallo) {
		this.fallo = fallo;
	}
	public String getSumilla() {
		return sumilla;
	}
	public void setSumilla(String sumilla) {
		this.sumilla = sumilla;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Resolucion(String id, String numero, String fecha, String fallo, String sumilla, String observacion) {
		super();
		this.id = id;
		this.numero = numero;
		this.fecha = fecha;
		this.fallo = fallo;
		this.sumilla = sumilla;
		this.observacion = observacion;
	}
	public Resolucion() {
		
	}
}
