package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="rc_pais")
@Entity
@Data
public class RcPais {
	
	@Id
	@Column(name="id_pais")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_pais")
	private String denominacion;
	
	@Column(name="s_estado")
	private String estado;
	
}
