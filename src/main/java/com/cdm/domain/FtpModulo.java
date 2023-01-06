package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import lombok.Data;

@Proxy(lazy = false)
@Table(name="ftp_modulos")
@Entity
@Data
public class FtpModulo {
	
	@Id
	@Column(name="n_id_ftp")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_ip")
	private String ip;
	
	@Column(name="s_usuario")
	private String usuario;
	
	@Column(name="s_clave")
	private String clave;
	
	@Column(name="n_puerto")
	private Integer puerto;
	
}
