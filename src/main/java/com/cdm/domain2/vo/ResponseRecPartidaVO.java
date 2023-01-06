package com.cdm.domain2.vo;

import java.util.List;

import com.cdm.domain2.RecPartidaArchivo;

import lombok.Data;

@Data
public class ResponseRecPartidaVO {

	private Integer id;

	private String documento;
	
	private String nombres;

	private String apePaterno;
	
	private String apeMaterno;
	
	private String fechaSistema;
	
	private String estado;
	
	private String usuario;
	
	private List<RecPartidaArchivo> archivoRecPartidaS;
}
