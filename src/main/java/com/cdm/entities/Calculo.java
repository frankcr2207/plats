package com.cdm.entities;

public class Calculo {
	private String fecha;
	private String fecha2;
	private int dia;
	private int mes;
	private int anio;
	private String operacion;
	
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getFecha2() {
		return fecha2;
	}

	public void setFecha2(String fecha2) {
		this.fecha2 = fecha2;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}
	
	public Calculo(String fecha, String fecha2, int dia, int mes, int anio, String operacion) {
		super();
		this.fecha = fecha;
		this.fecha2 = fecha2;
		this.dia = dia;
		this.mes = mes;
		this.anio = anio;
		this.operacion = operacion;
	}

	public Calculo() {
		
	}
}
