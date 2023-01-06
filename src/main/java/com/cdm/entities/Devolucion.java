package com.cdm.entities;

public class Devolucion {
	private String id;
	private String motivo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public Devolucion(String id, String motivo) {
		super();
		this.id = id;
		this.motivo = motivo;
	}
	public Devolucion() {
		
	}
}
