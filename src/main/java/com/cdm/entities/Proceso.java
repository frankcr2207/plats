package com.cdm.entities;

public class Proceso {
	private int id_expediente;
	private String expediente;
	private String id_instancia;
	private String instancia;
	private String id_sede;
	private String sede;
	private int id_secretario;
	private String secretario;
	private float reparacion;
	private double acumulado;
	private String medidas;
	private String inicio_medida;
	private String fin_medida;
	private String estado;
	public int getId_expediente() {
		return id_expediente;
	}
	public void setId_expediente(int id_expediente) {
		this.id_expediente = id_expediente;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getId_instancia() {
		return id_instancia;
	}
	public void setId_instancia(String id_instancia) {
		this.id_instancia = id_instancia;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public String getId_sede() {
		return id_sede;
	}
	public void setId_sede(String id_sede) {
		this.id_sede = id_sede;
	}
	public String getSede() {
		return sede;
	}
	public void setSede(String sede) {
		this.sede = sede;
	}
	public int getId_secretario() {
		return id_secretario;
	}
	public void setId_secretario(int id_secretario) {
		this.id_secretario = id_secretario;
	}
	public String getSecretario() {
		return secretario;
	}
	public void setSecretario(String secretario) {
		this.secretario = secretario;
	}
	public float getReparacion() {
		return reparacion;
	}
	public void setReparacion(float reparacion) {
		this.reparacion = reparacion;
	}
	public double getAcumulado() {
		return acumulado;
	}
	public void setAcumulado(double acumulado) {
		this.acumulado = acumulado;
	}
	public String getMedidas() {
		return medidas;
	}
	public void setMedidas(String medidas) {
		this.medidas = medidas;
	}
	public String getInicio_medida() {
		return inicio_medida;
	}
	public void setInicio_medida(String inicio_medida) {
		this.inicio_medida = inicio_medida;
	}
	public String getFin_medida() {
		return fin_medida;
	}
	public void setFin_medida(String fin_medida) {
		this.fin_medida = fin_medida;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Proceso(int id_expediente, String expediente, String id_instancia, String instancia, String id_sede,
			String sede, int id_secretario, String secretario, float reparacion, double acumulado,
			String medidas, String inicio_medida, String fin_medida, String estado) {
		super();
		this.id_expediente = id_expediente;
		this.expediente = expediente;
		this.id_instancia = id_instancia;
		this.instancia = instancia;
		this.id_sede = id_sede;
		this.sede = sede;
		this.id_secretario = id_secretario;
		this.secretario = secretario;
		this.reparacion = reparacion;
		this.acumulado = acumulado;
		this.medidas = medidas;
		this.inicio_medida = inicio_medida;
		this.fin_medida = fin_medida;
		this.estado = estado;
	}
	public Proceso() {
		
	}
}
