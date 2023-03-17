package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="sunarp_doc")
@Entity
@Data
public class SunarpSolicitudArchivo {

	@Id
	@Column(name="n_id_sunarp_doc")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="n_id_solicitud")
	private Integer idSolicitud;
	
	@Column(name="x_descripcion")
	private String descripcion;
	
	@Column(name="s_file_pdf")
	private String archivo;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;
	
}
