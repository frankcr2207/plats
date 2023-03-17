package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="sunarpsolicitud")
@Entity
@Data
public class SunarpSolicitud {

	@Id
	@Column(name="n_id_solicitud")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="n_id_tipo")
	private Integer tipo;
	
	@Column(name="s_documento")
	private String documento;
	
	@Column(name="s_nombre")
	private String imputado;
	
	@Column(name="x_expediente")
	private String expediente;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="c_instancia")
	private String idInstancia;
	
	@Column(name="c_sede")
	private String idSede;
	
	@Column(name="c_usuario_csjar")
	private String usuario;
	
}
