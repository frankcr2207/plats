package com.cdm.domain2.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain2.SunarpSolicitudArchivo;

import lombok.Data;

@Data
public class ResponseSunarpSolicitudVO {
	
	private Integer id;
	private Integer tipo;
	private String documento;
	private String imputado;
	private String expediente;
	private String estado;
	private String idSede;
	private String sede;
	private String idInstancia;
	private String instancia;
	private String observacion;
	private String usuario;
	private LocalDateTime fecIngreso;
	private List<SunarpSolicitudArchivo> archivos;
	private LocalDateTime fecAtencion;
	
}
