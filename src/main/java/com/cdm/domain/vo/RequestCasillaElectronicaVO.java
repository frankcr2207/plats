package com.cdm.domain.vo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RequestCasillaElectronicaVO {
	
	private Integer idCasillaElectronica;
	private String estado;
	private String respuesta;
	private MultipartFile adjunto;
	private String usuario;
	
}
