package com.cdm.domain.vo;

import lombok.Data;

@Data
public class RequestActaVO {
	
	private Integer orden;
	private String fecAudiencia;
	private String fecCreacion;
	private String expediente;
	private String instancia;
	private String estado;
}
