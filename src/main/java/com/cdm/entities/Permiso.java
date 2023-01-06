package com.cdm.entities;

public class Permiso {
	private String nombres;
	private int perfil;
	private String especialidad;
	private int cdm;
	private int cdg;
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public int getPerfil() {
		return perfil;
	}
	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
	public int getCdm() {
		return cdm;
	}
	public void setCdm(int cdm) {
		this.cdm = cdm;
	}
	public int getCdg() {
		return cdg;
	}
	public void setCdg(int cdg) {
		this.cdg = cdg;
	}
	public Permiso(String nombres, int perfil, String especialidad, int cdm, int cdg) {
		super();
		this.nombres = nombres;
		this.perfil = perfil;
		this.especialidad = especialidad;
		this.cdm = cdm;
		this.cdg = cdg;
	}
	public Permiso() {
		super();
	}
	
}
