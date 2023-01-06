package com.cdm.entities;

public class Respuesta {
	private String dni;
	private String examen;
	private String pregunta;
	private int movimiento;
	private int eleccion;

	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getExamen() {
		return examen;
	}
	public void setExamen(String examen) {
		this.examen = examen;
	}
	public String getPregunta() {
		return pregunta;
	}
	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}
	public int getMovimiento() {
		return movimiento;
	}
	public void setMovimiento(int movimiento) {
		this.movimiento = movimiento;
	}
	public int getEleccion() {
		return eleccion;
	}
	public void setEleccion(int eleccion) {
		this.eleccion = eleccion;
	}

	public Respuesta(String dni, String examen, String pregunta, int movimiento, int eleccion) {
		super();
		this.dni = dni;
		this.examen = examen;
		this.pregunta = pregunta;
		this.movimiento = movimiento;
		this.eleccion = eleccion;
	}
	public Respuesta() {
		
	}
}
