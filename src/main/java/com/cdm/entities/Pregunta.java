package com.cdm.entities;

import java.util.List;

public class Pregunta {
	
	private int id;
	private String nombre;
	private List<Integer> idAlternativas;
	private List<String> respuesta;
	private List<String> alternativas;
	private int boton1;
	private int boton2;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Integer> getIdAlternativas() {
		return idAlternativas;
	}

	public void setIdAlternativas(List<Integer> idAlternativas2) {
		this.idAlternativas = idAlternativas2;
	}
	
	public List<String> getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(List<String> respuesta) {
		this.respuesta = respuesta;
	}

	public List<String> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(List<String> alternativas) {
		this.alternativas = alternativas;
	}
	
	public int getBoton1() {
		return boton1;
	}

	public void setBoton1(int boton1) {
		this.boton1 = boton1;
	}

	public int getBoton2() {
		return boton2;
	}

	public void setBoton2(int boton2) {
		this.boton2 = boton2;
	}

	public Pregunta(int id, String nombre, List<Integer> idAlternativas, List<String> respuesta, List<String> alternativas, int boton1, int boton2) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.idAlternativas = idAlternativas;
		this.respuesta = respuesta;
		this.alternativas = alternativas;
		this.boton1 = boton1;
		this.boton2 = boton2;
	}

	public Pregunta() {
		
	}
}
