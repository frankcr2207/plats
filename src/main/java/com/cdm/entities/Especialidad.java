package com.cdm.entities;

public class Especialidad {
	private String c_especialidad;
	private String s_especialidad;
	public String getC_especialidad() {
		return c_especialidad;
	}
	public void setC_especialidad(String c_especialidad) {
		this.c_especialidad = c_especialidad;
	}
	public String getS_especialidad() {
		return s_especialidad;
	}
	public void setS_especialidad(String s_especialidad) {
		this.s_especialidad = s_especialidad;
	}
	public Especialidad(String c_especialidad, String s_especialidad) {
		super();
		this.c_especialidad = c_especialidad;
		this.s_especialidad = s_especialidad;
	}
	public Especialidad() {
		super();
	}
}
