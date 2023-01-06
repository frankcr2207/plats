package com.cdm.domain2;

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

@Table(name="rectificacion_doc")
@Entity
@Data
public class RecPartidaArchivo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="n_id_doc_rectificacion")
	private Integer id;
	
	@Column(name="n_id_doc")
	private Integer tipo;
	
	@Column(name="s_file")
	private String nombre;
	
	@Column(name="s_carpeta")
	private String carpeta;

}
