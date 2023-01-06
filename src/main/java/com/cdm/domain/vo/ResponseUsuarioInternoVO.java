package com.cdm.domain.vo;

import lombok.Data;

@Data
public class ResponseUsuarioInternoVO {
	
	private String usuario;
	
	private String nombres;
	
	private String apePaterno;
	
	private String apeMaterno;
	
	private String dni;
	
	private String clave;
	
	private String estado;
	
	private Integer perfil;
	
}
