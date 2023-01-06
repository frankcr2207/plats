package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;


@Table(name="instancia")
@Entity
@Data
public class Instancia {
	
	@Id
	@Column(name="c_instancia")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@Column(name="x_nom_instancia")
	private String denominacion;
	
	@Column(name="s_penal")
	private String especialidad;
	
	@Column(name="s_salvaguardia")
	private String salvaguardia;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_sede")
	private Sede sede;
}
