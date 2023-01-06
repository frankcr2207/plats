package com.cdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="casillaelectronica_archivos")
@Entity
@Data
public class ArchivoCasillaElectronica {
	
	@Id
	@Column(name="n_id_casillaelectronica_archivos")
	private Integer id;
	
	@Column(name="s_file_pdf")
	private String nombre;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;
	
}
