package com.cdm.domain.vo;

import lombok.Data;

@Data
public class ResponseUsuarioExternoVO {
	private Integer id;
	
	private String nombres;

	private String apellidos;
	
	private String documento;
	
	private String direccion;
	
	private String celular;
	
	private String email;
	
	private String clave;
}
