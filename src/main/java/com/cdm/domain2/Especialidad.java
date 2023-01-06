package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="especialidad")
@Entity
@Data
public class Especialidad {
	
	@Id
	@Column(name="c_especialidad")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@Column(name="s_especialidad")
	private String denominacion;
	
	@Column(name="s_activo")
	private String estado;
}
