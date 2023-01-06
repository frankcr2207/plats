package com.cdm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Table(name="cdg_archivos")
@Entity
@Data
public class CdgDocumentoArchivo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="n_id_cdg_archivos")
	private Integer id;
	
	@Column(name="s_file_pdf")
	private String nombre;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;

}
