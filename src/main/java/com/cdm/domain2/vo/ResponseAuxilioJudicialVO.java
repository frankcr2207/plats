package com.cdm.domain2.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain2.AuxilioJudicialArchivo;
import com.cdm.domain2.Especialidad;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseAuxilioJudicialVO {

	private Integer idAuxilioJudicial;

	private String documento;

	private String nombres;
	
	private String apePaterno;
	
	private String apeMaterno;
	
	private String direccion;
	
	private String sede;
	
	private String estado;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime fechaSistema;
	
	private String usuario;
	
	private LocalDateTime fechaAtencion;
	
	private String observacion;
	
	private List<AuxilioJudicialArchivo> archivoAuxJudS;
	
	private Especialidad especialidad;
}
