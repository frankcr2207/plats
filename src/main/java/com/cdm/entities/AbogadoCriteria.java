package com.cdm.entities;

public class AbogadoCriteria {
	private String dni;
	private String estado;
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public AbogadoCriteria(String dni, String estado) {
		super();
		this.dni = dni;
		this.estado = estado;
	}
	
}
