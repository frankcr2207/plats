package com.cdm.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="rc_reglas")
@Entity
@Data
public class RcRegla {
	
	@Id
	@Column(name="n_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="f_regla")
	private LocalDateTime fechaRegla;
	
	@Column(name="s_observaciones")
	private String observacion;
	
	@Column(name="s_estado")
	private String estado;
	
}
