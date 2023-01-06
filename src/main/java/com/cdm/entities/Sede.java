package com.cdm.entities;

public class Sede {
	private String c_sede;
	private String s_sede;
	public String getC_sede() {
		return c_sede;
	}
	public void setC_sede(String c_sede) {
		this.c_sede = c_sede;
	}
	public String getS_sede() {
		return s_sede;
	}
	public void setS_sede(String s_sede) {
		this.s_sede = s_sede;
	}
	public Sede(String c_sede, String s_sede) {
		super();
		this.c_sede = c_sede;
		this.s_sede = s_sede;
	}
	public Sede() {
		
	}
}
