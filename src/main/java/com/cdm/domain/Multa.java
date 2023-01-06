package com.cdm.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Table(name="multa")
@Entity
@Data
public class Multa {
	@Id
	@Column(name="n_id_multa")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="x_expediente")
	private String expediente;
	
	@Column(name="s_numero")
	private String numExp;
	
	@Column(name="s_anio")
	private String anioExp;
	
	@Column(name="s_incidente")
	private String incExp;
	
	@Column(name="n_monto")
	private float monto;
	
	@Column(name="f_resolucion")
	private LocalDateTime fecResolucion;
	
	@Column(name="n_resolucion")
	private Integer numResolucion;
	
	@Column(name="s_consentida")
	private String consentida;
	
	@Column(name="s_remitida")
	private String remitida;
	
	@Column(name="s_tipo_multa")
	private String tipoMulta;
	
	@Column(name="s_tipo_multado")
	private String tipoMultado;
	
	@Column(name="s_documento")
	private String documento;
	
	@Column(name="s_razon_social")
	private String razonSocial;
	
	@Column(name="s_nombres")
	private String nombres;
	
	@Column(name="s_apellidos")
	private String apellidos;
	
	@Column(name="s_correo")
	private String correo;
	
	@Column(name="s_celular")
	private String celular;
	
	@Column(name="s_domicilio_real")
	private String domReal;
	
	@Column(name="s_domicilio_procesal")
	private String domProcesal;
	
	@Column(name="s_casilla_fisica")
	private String casillaFisica;
	
	@Column(name="s_casilla_electronica")
	private String casillaElectronica;
	
	@Column(name="f_registro")
	private LocalDateTime fecSistema;
	
	@Column(name="f_gestion")
	private LocalDateTime fecGestion;
	
	@Column(name="s_observacion")
	private String observacion;
	
	@Column(name="s_estado")
	private String estado;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_instancia")
	private Instancia instancia;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_sede")
	private Sede sede;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_especialidad")
	private Especialidad especialidad;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_usuario_sec")
	private UsuarioInterno secretario;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_usuario_sj")
	private UsuarioInterno servJudiciales;
	
}
