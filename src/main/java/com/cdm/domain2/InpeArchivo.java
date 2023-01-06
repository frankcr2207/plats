package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="inpe_documento")
@Entity
@Data
public class InpeArchivo {

	@Id
	@Column(name="n_id_doc_inpe")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_file")
	private String archivo;
	
	@Column(name="s_carpeta")
	private String carpeta;
	
}
