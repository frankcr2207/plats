package com.cdm.domain.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAudienciaVO {
	
	private Integer id;
	private String expediente;
	@JsonProperty(value = "fechaHora")
	private LocalDateTime fecAudiencia;
	private String idInstancia;
	private String idSede;
	private String especialidad;
	private String instancia;
	private String delito;
	private String tipo;
	private String parte1;
	private String parte2;
	private String enlace;
	private String estado;
	
}
