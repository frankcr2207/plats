package com.cdm.domain2.vo;

import lombok.Data;

@Data
public class ResponseArchivoAuxJudVO {
	
	private Integer id;
	
	private Integer idAuxilioJudicial;
	
	private Integer tipo;

	private String nombre;
	
	private String carpeta;
}
