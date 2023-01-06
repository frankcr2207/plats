package com.cdm.entities;

public class Derivacion {
	private String id;
	private String secretario;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSecretario() {
		return secretario;
	}
	public void setSecretario(String secretario) {
		this.secretario = secretario;
	}
	public Derivacion(String id, String secretario) {
		super();
		this.id = id;
		this.secretario = secretario;
	}
	public Derivacion() {
		
	}
}
