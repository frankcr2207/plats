package com.cdm.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Table(name="casillaelectronica")
@Entity
@Data
public class CasillaElectronica {
	
	@Id
	@Column(name="n_id_casillaelectronica")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idCasillaElectronica;
	
	@Column(name="s_tipo_institucion")
	private String institucion;
	
	@Column(name="n_id_tipo")
	private Integer idTipo;
	
	@Column(name="s_tipo")
	private String tipo;
	
	@Column(name="s_ruc")
	private String ruc;
	
	@Column(name="s_direccion")
	private String direccion;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="s_telefono")
	private String telefono;
	
	@Column(name="s_registro")
	private String registro;
	
	@Column(name="s_solicitud_casilla")
	private String solicitudCasilla;
	
	@Column(name="f_sistema")
	private LocalDateTime fechaSistema;
	
	@Column(name="s_respuesta")
	private String respuesta;
	
	@Column(name="s_file_pdf_respuesta")
	private String adjuntoRespuesta;
	
	@Column(name="s_carpeta_pdf_respuesta")
	private String carpetaRespuesta;
	
	@Column(name="f_respuesta")
	private LocalDateTime fechaRespuesta;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_departamento")
	private Departamento departamento;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_provincia")
	private Provincia provincia;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_distrito")
	private Distrito distrito;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_usuario")
	private UsuarioExterno usuarioExterno;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_casillaelectronica")
	private List<ArchivoCasillaElectronica> archivoCasillaElectronicaS;
	
}
