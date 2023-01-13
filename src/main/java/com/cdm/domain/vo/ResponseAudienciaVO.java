package com.cdm.domain.vo;

import java.time.LocalDateTime;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAudienciaVO {
	
	private Integer id;
	private String expediente;
	private String idSede;
	private String idInstancia;
	private String instancia;
	private String especialidad;
	private String delito;
	private String parte1;
	private String parte2;
	private String jueces;
	private String tipo;
	private String reservado;
	private String enlace;
	private String idEstado;
	private String estado;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	private LocalDateTime fecAudiencia;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	@JsonProperty(value = "hora")
	private LocalDateTime hora;
	
	private LocalDateTime fecSistema;
	
}
