package com.cdm.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.cdm.domain.CdgDocumentoArchivo;
import com.cdm.domain.Instancia;
import com.cdm.domain.Sede;
import com.cdm.domain.UsuarioExterno;
import com.cdm.domain.UsuarioInterno;

import lombok.Data;

@Data
public class ResponseCdgDocumentoVO {
	
	private Integer id;
	
	private String expediente;
	
	private String archivo;
	
	private String carpeta;
	
	private String estado;
	
	private LocalDateTime fecSistema;
	
	//private ResponseUsuarioInternoVO usuarioInterno;
	private String usuarioInterno;
	
	private String respuesta;
	
	private LocalDateTime fecAtencion;
	
	private String ip;
	
	private String superior;
	
	private ResponseCdgTipoDocumentoVO cdgTipoDocumento;
	
	private ResponseUsuarioExternoVO usuarioExterno;
	
	private ResponseInstanciaVO instancia;
	
	private ResponseSedeVO sede;
	
	private List<CdgDocumentoArchivo> archivos;
}
