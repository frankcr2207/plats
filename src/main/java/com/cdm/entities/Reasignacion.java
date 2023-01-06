package com.cdm.entities;

public class Reasignacion {
	private String origen;
	private String valor;
	private String destino;
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public Reasignacion() {
		
	}
	public Reasignacion(String origen, String valor, String destino) {
		super();
		this.origen = origen;
		this.valor = valor;
		this.destino = destino;
	}
	
}
