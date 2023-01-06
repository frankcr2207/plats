package com.cdm.entities;

public class Regla {
	private int id;
	private String registro;
	private String regla;
	private float reparacion;
	private String estado;
	private String ruta;
	private String archivo;
	private String observaciones;
	private String usuario;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRegistro() {
		return registro;
	}
	public void setRegistro(String registro) {
		this.registro = registro;
	}
	public String getRegla() {
		return regla;
	}
	public void setRegla(String regla) {
		this.regla = regla;
	}
	public float getReparacion() {
		return reparacion;
	}
	public void setReparacion(float reparacion) {
		this.reparacion = reparacion;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String getArchivo() {
		return archivo;
	}
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Regla(int id, String registro, String regla, float reparacion, String ruta, String archivo, String estado, String observaciones, String usuario) {
		super();
		this.id = id;
		this.registro = registro;
		this.regla = regla;
		this.reparacion = reparacion;
		this.ruta = ruta;
		this.archivo = archivo;
		this.estado = estado;
		this.observaciones = observaciones;
		this.usuario = usuario;
	}
	public Regla() {
		
	}
}
