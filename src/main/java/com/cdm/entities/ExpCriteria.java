package com.cdm.entities;

public class ExpCriteria {
	private String n_unico;
	private String x_formato;
	public String getN_unico() {
		return n_unico;
	}
	public void setN_unico(String n_unico) {
		this.n_unico = n_unico;
	}
	public String getX_formato() {
		return x_formato;
	}
	public void setX_formato(String x_formato) {
		this.x_formato = x_formato;
	}
	public ExpCriteria(String n_unico, String x_formato) {
		super();
		this.n_unico = n_unico;
		this.x_formato = x_formato;
	}
	public ExpCriteria(){
		
	}
}
