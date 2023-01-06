package com.cdm.entities;

public class Instancia {
	private String c_instancia;
	private String x_nom_instancia;
	public String getC_instancia() {
		return c_instancia;
	}
	public void setC_instancia(String c_instancia) {
		this.c_instancia = c_instancia;
	}
	public String getX_nom_instancia() {
		return x_nom_instancia;
	}
	public void setX_nom_instancia(String x_nom_instancia) {
		this.x_nom_instancia = x_nom_instancia;
	}
	public Instancia(String c_instancia, String x_nom_instancia) {
		super();
		this.c_instancia = c_instancia;
		this.x_nom_instancia = x_nom_instancia;
	}
	
	public Instancia() {
		
	}
}
