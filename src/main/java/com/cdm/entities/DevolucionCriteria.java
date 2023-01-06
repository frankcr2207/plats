package com.cdm.entities;

public class DevolucionCriteria {
	private String secretario;
	private String fecha;
	private String observacion;
	public String getSecretario() {
		return secretario;
	}
	public void setSecretario(String secretario) {
		this.secretario = secretario;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public DevolucionCriteria(String secretario, String fecha, String observacion) {
		super();
		this.secretario = secretario;
		this.fecha = fecha;
		this.observacion = observacion;
	}
	public DevolucionCriteria() {
		
	}
}
