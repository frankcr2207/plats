package com.cdm.entities;

public class Parametros{
	private String token;
	private String param1;
	private String param2;
	private String param3;
	public Parametros(String token, String param1, String param2, String param3) {
		super();
		this.token = token;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}
	public Parametros() {
		super();
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	
}
