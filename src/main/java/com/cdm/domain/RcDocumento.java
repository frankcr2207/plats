package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="rc_documento")
@Entity
@Data
public class RcDocumento {

	@Id
	@Column(name="n_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_documento")
	private String denominacion;
	
	@Column(name="s_abreviacion")
	private String abreviacion;
	
}
