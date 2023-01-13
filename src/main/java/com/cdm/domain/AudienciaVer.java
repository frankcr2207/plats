package com.cdm.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="audiencia_ver")
@Entity
@Data
public class AudienciaVer {

	@Id
	@Column(name="id_audiencia_ver")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="id_audiencia")
	private Integer idAudiencia;
	
	@Column(name="s_link_audiencia")
	private String enlace;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="n_version")
	private Integer version;
	
	@Column(name="s_ip")
	private String ip;
	
	@Column(name="c_usuario")
	private String usuario;
	
	@Column(name="fecha_registro")
	private LocalDateTime fecSistema;
	
}
