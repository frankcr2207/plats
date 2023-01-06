package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="tipo_documento")
@Entity
@Data
public class CdgTipoDocumento {
	
	@Id
	@Column(name="n_id_tipo_documento")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_tipo_documento")
	private String denominacion;
	
}
