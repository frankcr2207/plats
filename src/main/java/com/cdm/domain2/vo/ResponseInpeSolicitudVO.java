package com.cdm.domain2.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ResponseInpeSolicitudVO {

	private Integer idSolicitud;
	
	private String expediente;
	
	private String anterior;
	
	private String nuevo;
	
	private String estado;
	
	private String codigo;
	
	private String observacion;
	
	private String usuario;
	
	private LocalDateTime fechaSistema;
	
	private LocalDateTime fechaAtencion;
	
	private String instancia;
	
	private String sede;
	
	private List<ResponseInpeArchivos> inpeArchivos;
}
