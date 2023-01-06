package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import lombok.Data;

@Proxy(lazy = false)
@Table(name="ftp_cdg")
@Entity
@Data
public class FtpCdg {
	
	@Id
	@Column(name="c_sede")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String sede;
	
	@Column(name="s_ip")
	private String ip;
	
	@Column(name="s_user")
	private String usuario;
	
	@Column(name="s_password")
	private String clave;
	
	@Column(name="n_puerto")
	private Integer puerto;
}
