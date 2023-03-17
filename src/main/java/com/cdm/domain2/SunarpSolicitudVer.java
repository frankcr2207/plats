package com.cdm.domain2;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="sunarp_estado")
@Entity
@Data
public class SunarpSolicitudVer {
	
	@Id
	@Column(name="n_id_sunarp_estados")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="n_id_solicitud")
	private Integer idSolicitud;
	
	@Column(name="x_observacion")
	private String observacion;
	
	@Column(name="f_sistema")
	private LocalDateTime fecSistema;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="s_ultimo")
	private String ultimo;
	
	@Column(name="s_ip_pc")
	private String ip;
	
	@Column(name="c_usuario_csjar")
	private String usuario;
	
}
