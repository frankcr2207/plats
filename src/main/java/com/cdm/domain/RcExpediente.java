package com.cdm.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Table(name="rc_expediente")
@Entity
@Data
public class RcExpediente {
	
	@Id
	@Column(name="id_expediente")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="n_expediente")
	private String expediente;
	
	@Column(name="s_estado")
	private String estado;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_instancia")
	private Instancia instancia;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_sede")
	private Sede sede;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_expediente")
	private List<RcRegla> reglas;
	
}
