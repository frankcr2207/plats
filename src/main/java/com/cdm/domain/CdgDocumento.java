package com.cdm.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Table(name="cdg")
@Entity
@Data
public class CdgDocumento {
	
	@Id
	@Column(name="n_id_cdg")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="x_expediente")
	private String expediente;
	
	@Column(name="s_file_pdf")
	private String archivo;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="f_sistema")
	private LocalDateTime fecSistema;
	
	@Column(name="s_respuesta")
	private String respuesta;
	
	@Column(name="f_sistema_respuesta")
	private LocalDateTime fecAtencion;
	
	@Column(name="s_ip_pc_respuesta")
	private String ip;
	
	@Column(name="s_superior")
	private String superior;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_id_tipo_documento")
	private CdgTipoDocumento cdgTipoDocumento;
	
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "c_usuario_asignado")
	@Column(name="c_usuario_asignado")
	private String usuarioInterno;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_id_usuario")
	private UsuarioExterno usuarioExterno;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_instancia")
	private Instancia instancia;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_sede")
	private Sede sede;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_id_cdg")
	private List<CdgDocumentoArchivo> archivos;
	
}
