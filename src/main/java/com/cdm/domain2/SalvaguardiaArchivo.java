package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="salvaguardia_doc")
@Entity
@Data
public class SalvaguardiaArchivo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="n_id_doc_salvaguardia")
	private Integer id;
	
	@Column(name="n_id_doc")
	private Integer tipo;
	
	@Column(name="s_file")
	private String nombre;
	
	@Column(name="s_carpeta")
	private String carpeta;
}
