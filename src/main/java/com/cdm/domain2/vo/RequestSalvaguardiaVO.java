package com.cdm.domain2.vo;

import lombok.Data;

@Data
public class RequestSalvaguardiaVO {
	
	private Integer idSalvaguardia;
	private String tipoRespuesta;
	private String textoRespuesta;
	private String codigo;
	private String expediente;
	private String instancia;
	private String anio;
	private String organo;
	private String especialidad;
	private String usuario;
}
