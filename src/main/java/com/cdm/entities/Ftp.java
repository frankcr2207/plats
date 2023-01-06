package com.cdm.entities;

public class Ftp {
	private String ip;
	private String usuario;
	private String clave;
	private int puerto;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	public int getPuerto() {
		return puerto;
	}
	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	public Ftp(String ip, String usuario, String clave, int puerto) {
		super();
		this.ip = ip;
		this.usuario = usuario;
		this.clave = clave;
		this.puerto = puerto;
	}
	
	public Ftp() {
		
	}
}
