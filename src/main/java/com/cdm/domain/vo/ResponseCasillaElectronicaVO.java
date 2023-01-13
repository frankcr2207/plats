package com.cdm.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.cdm.domain.ArchivoCasillaElectronica;
import com.cdm.domain.Departamento;
import com.cdm.domain.Distrito;
import com.cdm.domain.Provincia;
import com.cdm.domain.UsuarioExterno;

import lombok.Data;

@Data
public class ResponseCasillaElectronicaVO {
	
	private Integer idCasillaElectronica;
	
	private String institucion;
	
	private Integer idTipo;
	
	private String tipo;
	
	private String solicitudCasilla;
	
	private String ruc;
	
	private String direccion;
	
	private String telefono;
	
	private String registro;
	
	private String estado;
	
	private LocalDateTime fechaSistema;
	
	private Departamento departamento;
	
	private Provincia provincia;
	
	private Distrito distrito;
	
	private UsuarioExterno usuarioExterno;
	
	private List<ArchivoCasillaElectronica> archivoCasillaElectronicaS;

}
