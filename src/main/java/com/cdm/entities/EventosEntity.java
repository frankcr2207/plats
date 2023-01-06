package com.cdm.entities;

public class EventosEntity {

	private String sala;
	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}
	private String title;
	private String date;
	private String start;
	private String end;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	private String color;
	private String url;
	
	public EventosEntity(){
		
	}	
	
	public EventosEntity(String sala, String title, String date, String start, String end, String color, String url) {
		super();
		this.sala = sala;
		this.title = title;
		this.date = date;
		this.start = start;
		this.end = end;
		this.color = color;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
