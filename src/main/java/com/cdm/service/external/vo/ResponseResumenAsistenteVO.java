package com.cdm.service.external.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResumenAsistenteVO {
	private String id;
	private String nombres;
	private String apellidos;
	private Integer totalAudiencias;
	private Integer actasIncompletas;
	private List<ResponseAudienciaActaVO> audienciaActas;
}

