package com.cdm.entities;

public class Audiencia {
	private String expediente;
	private String instancia;
	private String tipo;
	private String jueces;
	private String parte1;
	private String parte2;
	private String inicio;
	private String delito;
	private String enlace;
	
	public String getInicio() {
		return inicio;
	}
	public void setInicio(String inicio) {
		this.inicio = inicio;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getJueces() {
		return jueces;
	}
	public void setJueces(String jueces) {
		this.jueces = jueces;
	}
	public String getParte1() {
		return parte1;
	}
	public void setParte1(String parte1) {
		this.parte1 = parte1;
	}
	public String getParte2() {
		return parte2;
	}
	public void setParte2(String parte2) {
		this.parte2 = parte2;
	}
	public String getDelito() {
		return delito;
	}
	public void setDelito(String delito) {
		this.delito = delito;
	}
	public String getEnlace() {
		return enlace;
	}
	public void setEnlace(String enlace) {
		this.enlace = enlace;
	}
	public Audiencia(String expediente, String instancia, String tipo, String jueces, String parte1, String parte2,  String inicio, String delito, String enlace) {
		super();
		this.expediente = expediente;
		this.instancia = instancia;
		this.tipo = tipo;
		this.jueces = jueces;
		this.parte1 = parte1;
		this.parte2 = parte2;
		this.inicio = inicio;
		this.delito = delito;
		this.enlace = enlace;
	}
	public Audiencia() {
		
	}
}
