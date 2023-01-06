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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.cdm.domain.Instancia;
import com.cdm.domain.Sede;

import lombok.Data;

@Table(name="inpe_solicitud")
@Entity
@Data
public class InpeSolicitud {
	
	@Id
	@Column(name="n_id_solicitud")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idSolicitud;
	
	@Column(name="x_expediente")
	private String expediente;
	
	@Column(name="s_dice")
	private String anterior;
	
	@Column(name="s_debe_decir")
	private String nuevo;
	
	@Column(name="s_carpeta_pdf")
	private String carpeta;
	
	@Column(name="s_file_pdf")
	private String archivo;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="s_codigo")
	private String codigo;
	
	@Column(name="x_observacion_ingreso")
	private String observacion;
	
	@Column(name="c_usuario_csjar")
	private String usuario;
	
	@Column(name="s_ip_pc")
	private String ip;
	
	@Column(name="f_sistema")
	private LocalDateTime fechaSistema;
	
	@Column(name="f_sistema_cdg")
	private LocalDateTime fechaAtencion;
	
	@Column(name = "c_instancia")
	private String instancia;
	
	@Column(name = "c_sede")
	private String sede;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_solicitud")
	private List<InpeArchivo> inpeArchivos;
}
