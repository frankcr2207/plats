package com.cdm.entities;

public class Resultado {
	private String id;
	private String fecha1;
	private String fecha2;
	
	public Resultado(String id, String fecha1, String fecha2) {
		super();
		this.id = id;
		this.fecha1 = fecha1;
		this.fecha2 = fecha2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFecha1() {
		return fecha1;
	}

	public void setFecha1(String fecha1) {
		this.fecha1 = fecha1;
	}

	public String getFecha2() {
		return fecha2;
	}

	public void setFecha2(String fecha2) {
		this.fecha2 = fecha2;
	}

	public Resultado() {
		
	}
}
