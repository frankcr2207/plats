package com.cdm.domain2;

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
import javax.persistence.Table;

import lombok.Data;

@Table(name="salvaguardia")
@Entity
@Data
public class Salvaguardia {
	
	@Id
	@Column(name="n_id_salvaguardia")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idSalvaguardia;
	
	@Column(name="s_paterno_solicitante")
	private String apePaterno;
	
	@Column(name="s_materno_solicitante")
	private String apeMaterno;
	
	@Column(name="s_nombres_solicitante")
	private String nombres;
	
	@Column(name="s_documento_solicitante")
	private String documento;
	
	@Column(name="s_direccion_solicitante")
	private String direccion;
	
	@Column(name="c_sede")
	private String sede;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;
	
	@Column(name="s_file_pdf")
	private String archivo;
	
	@Column(name="s_codigo")
	private String codigo;
	
	@Column(name="x_expediente")
	private String expediente;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="s_ip_pc")
	private String ip;
	
	@Column(name="f_sistema")
	private LocalDateTime fechaSistema;
	
	@Column(name="x_observacion_ingreso")
	private String observacion;
	
	@Column(name="c_usuario_csjar")
	private String usuario;
	
	@Column(name="f_sistema_cdg")
	private LocalDateTime fechaAtencion;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_salvaguardia")
	private List<SalvaguardiaArchivo> archivosSalvaguardia;
}
