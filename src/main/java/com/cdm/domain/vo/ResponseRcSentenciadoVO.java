package com.cdm.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseRcSentenciadoVO {
	
	private Integer id;
	
	private String documento;
	
	private String nombres;
	
	private String apellidos;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDateTime fechaNacimiento;
	
	private String celular;
	
	private String fijo;
	
	private String correo;
	
	private String vario;
	
	private String domicilio;
	
	private String referencia;
	
	private String trabaja;
	
	private String centroTrabajo;
	
	private String foto;
	
	private List<ResponseRcExpedienteVO> expedientes;
	
}
