package com.cdm.domain.vo;

import java.util.List;

import lombok.Data;

@Data
public class ResponseRcExpedienteVO {
	
	private Integer id;
	
	private String expediente;
	
	private String estado;
	
	private ResponseInstanciaVO instancia;
	
	private ResponseSedeVO sede;
	
	private List<ResponseRcReglaVO> reglas;
	
}
