package com.cdm.entities;

public class CalculoCriteria {
	private int anio1;
	private int anio2;
	private int mes1;
	private int mes2;
	private int dia1;
	private int dia2;
	public int getAnio1() {
		return anio1;
	}
	public void setAnio1(int anio1) {
		this.anio1 = anio1;
	}
	public int getAnio2() {
		return anio2;
	}
	public void setAnio2(int anio2) {
		this.anio2 = anio2;
	}
	public int getMes1() {
		return mes1;
	}
	public void setMes1(int mes1) {
		this.mes1 = mes1;
	}
	public int getMes2() {
		return mes2;
	}
	public void setMes2(int mes2) {
		this.mes2 = mes2;
	}
	public int getDia1() {
		return dia1;
	}
	public void setDia1(int dia1) {
		this.dia1 = dia1;
	}
	public int getDia2() {
		return dia2;
	}
	public void setDia2(int dia2) {
		this.dia2 = dia2;
	}
	public CalculoCriteria(int anio1, int anio2, int mes1, int mes2, int dia1, int dia2) {
		super();
		this.anio1 = anio1;
		this.anio2 = anio2;
		this.mes1 = mes1;
		this.mes2 = mes2;
		this.dia1 = dia1;
		this.dia2 = dia2;
	}
	public CalculoCriteria() {
		
	}
}
