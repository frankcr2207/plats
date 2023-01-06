package com.cdm.domain2.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain2.SalvaguardiaArchivo;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseSalvaguardiaVO {

	private Integer idSalvaguardia;
	
	private String apePaterno;
	
	private String apeMaterno;
	
	private String nombres;
	
	private String documento;
	
	private String direccion;
	
	private String expediente;
	
	private String sede;
	
	private String carpeta;
	
	private String archivo;
	
	private String codigo;
	
	private String estado;
	
	private String ip;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime fechaSistema;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime fechaAtencion;
	
	private String observacion;
	
	private String usuario;
	
	private List<SalvaguardiaArchivo> archivosSalvaguardia;
	
}
