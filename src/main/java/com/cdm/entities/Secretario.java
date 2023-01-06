package com.cdm.entities;

public class Secretario {
	private String c_usuario;
	private String nombresCompletos;
	public String getC_usuario() {
		return c_usuario;
	}
	public void setC_usuario(String c_usuario) {
		this.c_usuario = c_usuario;
	}
	public String getNombresCompletos() {
		return nombresCompletos;
	}
	public void setNombresCompletos(String nombresCompletos) {
		this.nombresCompletos = nombresCompletos;
	}
	public Secretario(String c_usuario, String nombresCompletos) {
		super();
		this.c_usuario = c_usuario;
		this.nombresCompletos = nombresCompletos;
	}
	public Secretario() {
	
	}
}
