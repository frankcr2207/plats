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
public class ResponseAudienciaActaExternalVO {
	
	private String id;
	private LocalDateTime fecAudiencia;
	private LocalDateTime fecCreacion;
	private LocalDateTime fecDescargo;
	private String documento;
	private String expediente;
	private String instancia;
	private Boolean estado;
	
}
