package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="sedeorganousuario")
@Entity
@Data
public class SedeUsuario {
	
	@Id
	@Column(name="n_id_sou")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="c_sede")
	private String sede;
	
	@Column(name="n_id_organo")
	private Integer organo;
	
	@Column(name="c_usuario")
	private String usuario;
}
