package com.cdm.entities;

public class Parametro {
	private String codigo;
	private String valor;
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public Parametro(String codigo, String valor) {
		super();
		this.codigo = codigo;
		this.valor = valor;
	}
	public Parametro() {
		
	}
}
