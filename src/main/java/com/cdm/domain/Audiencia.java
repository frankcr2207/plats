package com.cdm.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="audiencias")
@Entity
@Data
public class Audiencia {
	
	@Id
	@Column(name="id_audiencia")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="n_expediente")
	private String expediente;
	
	@Column(name="c_sede")
	private String idSede;
	
	@Column(name="c_instancia")
	private String idInstancia;
	
	@Column(name="c_especialidad")
	private String especialidad;
	
	@Column(name="s_instancia")
	private String instancia;
	
	@Column(name="s_delito")
	private String delito;
	
	@Column(name="s_agraviado")
	private String parte1;
	
	@Column(name="s_imputado")
	private String parte2;
	
	@Column(name="s_jueces")
	private String jueces;
	
	@Column(name="s_tipo_audiencia")
	private String tipo;
	
	@Column(name="s_reservado")
	private String reservado;
	
	@Column(name="s_link_audiencia")
	private String enlace;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="ip_audiencia")
	private String ip;
	
	@Column(name="fecha_audiencia")
	private LocalDateTime fecAudiencia;
	
	@Column(name="fecha_registro")
	private LocalDateTime fecSistema;
}
