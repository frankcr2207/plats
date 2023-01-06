package com.cdm.entities;

public class Abogado {
	private int id_abogado;
	private String s_dni;
	private String s_nombres;
	private String s_apellidos;
	private String s_casilla_fisica;
	private String s_casilla_electronica;
	private String s_domicilio_real;
	private String s_domicilio_procesal;
	private String estado;
	public int getId_abogado() {
		return id_abogado;
	}
	public void setId_abogado(int id_abogado) {
		this.id_abogado = id_abogado;
	}
	public String getS_dni() {
		return s_dni;
	}
	public void setS_dni(String s_dni) {
		this.s_dni = s_dni;
	}
	public String getS_nombres() {
		return s_nombres;
	}
	public void setS_nombres(String s_nombres) {
		this.s_nombres = s_nombres;
	}
	public String getS_apellidos() {
		return s_apellidos;
	}
	public void setS_apellidos(String s_apellidos) {
		this.s_apellidos = s_apellidos;
	}
	public String getS_casilla_fisica() {
		return s_casilla_fisica;
	}
	public void setS_casilla_fisica(String s_casilla_fisica) {
		this.s_casilla_fisica = s_casilla_fisica;
	}
	public String getS_casilla_electronica() {
		return s_casilla_electronica;
	}
	public void setS_casilla_electronica(String s_casilla_electronica) {
		this.s_casilla_electronica = s_casilla_electronica;
	}
	public String getS_domicilio_real() {
		return s_domicilio_real;
	}
	public void setS_domicilio_real(String s_domicilio_real) {
		this.s_domicilio_real = s_domicilio_real;
	}
	public String getS_domicilio_procesal() {
		return s_domicilio_procesal;
	}
	public void setS_domicilio_procesal(String s_domicilio_procesal) {
		this.s_domicilio_procesal = s_domicilio_procesal;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Abogado(int id_abogado, String s_dni, String s_nombres, String s_apellidos, String s_casilla_fisica,
			String s_casilla_electronica, String s_domicilio_real, String s_domicilio_procesal, String estado) {
		super();
		this.id_abogado = id_abogado;
		this.s_dni = s_dni;
		this.s_nombres = s_nombres;
		this.s_apellidos = s_apellidos;
		this.s_casilla_fisica = s_casilla_fisica;
		this.s_casilla_electronica = s_casilla_electronica;
		this.s_domicilio_real = s_domicilio_real;
		this.s_domicilio_procesal = s_domicilio_procesal;
		this.estado = estado;
	}
	public Abogado() {
		super();
	}
}
