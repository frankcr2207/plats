package com.cdm.service.external.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAudienciaActaVO {
	
	private Integer orden;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDateTime fecAudiencia;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime fecCreacion;
	private LocalDateTime fecDescargo;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime fecDescargoCorto;
	private String documento;
	private String expediente;
	private String instancia;
	private String estado;
	
}
