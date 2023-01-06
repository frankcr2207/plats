package com.cdm.domain.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseMultaVO {
	
	private Integer id;
	
	private String expediente;
	
	private float monto;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss a")
	private LocalDateTime fecResolucion;
	
	private Integer numResolucion;
	
	private String consentida;
	
	private String remitida;
	
	private String tipoMultado;
	
	private Integer documento;
	
	private String razonSocial;
	
	private String nombres;
	
	private String apellidos;
	
	private String correo;
	
	private String celular;
	
	private String domReal;

	private String domProcesal;
	
	private String casillaFisica;
	
	private String casillaElectronica;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss a")
	private LocalDateTime fecSistema;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss a")
	private LocalDateTime fecGestion;
	
	private String estado;
	
	private String observacion;
	
	private ResponseInstanciaVO instancia;

	private ResponseSedeVO sede;

	private ResponseEspecialidadVO especialidad;

	private ResponseUsuarioInternoVO secretario;
	
	private ResponseUsuarioInternoVO servJudiciales;
	
}
