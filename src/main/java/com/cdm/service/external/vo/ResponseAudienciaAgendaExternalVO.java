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
public class ResponseAudienciaAgendaExternalVO {
	private String expediente;
	private LocalDateTime fechaHora;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalDateTime hora;
	private String idInstancia;
	private String idSede;
	private String instancia;
	private String especialidad;
	private String tipo;
	private String jueces;
	private String parte1;
	private String parte2;
	private String delito;
	private String enlace;
}
