package com.cdm.entities;

public class Sala {
	private String id;
	private String sala;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSala() {
		return sala;
	}
	public void setSala(String sala) {
		this.sala = sala;
	}
	public Sala(String id, String sala) {
		super();
		this.id = id;
		this.sala = sala;
	}
	public Sala() {
		
	}
}
