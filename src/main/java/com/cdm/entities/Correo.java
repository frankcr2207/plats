package com.cdm.entities;

public class Correo {
	private String asunto;
	private String destino;
	private String contenido;
	private String archivo;
	
	public String getArchivo() {
		return archivo;
	}
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public Correo(String asunto, String destino, String contenido, String archivo) {
		super();
		this.asunto = asunto;
		this.destino = destino;
		this.contenido = contenido;
		this.archivo = archivo;
	}
	public Correo() {
		
	}
}
