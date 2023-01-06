package com.cdm.entities;

public class InstanciaCriteria {
	private String instancia;
	private String usuario;
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public InstanciaCriteria(String usuario, String instancia) {
		super();
		this.instancia = instancia;
		this.usuario = usuario;
	}
	public InstanciaCriteria() {
		
	}
}
