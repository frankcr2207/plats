package com.cdm.entities;

public class Cdm {
	private int id;
	private String nombres;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public Cdm(int id, String nombres) {
		super();
		this.id = id;
		this.nombres = nombres;
	}
	public Cdm(){
		
	}
}
