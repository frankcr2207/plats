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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Table(name="rc_sentenciados")
@Entity
@Data
public class RcSentenciado {
	
	@Id
	@Column(name="id_sentenciado")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_documento")
	private String documento;
	
	@Column(name="s_nombres")
	private String nombres;
	
	@Column(name="s_apellidos")
	private String apellidos;
	
	@Column(name="f_nacimiento")
	private LocalDateTime fechaNacimiento;
	
	@Column(name="n_celular")
	private String celular;
	
	@Column(name="n_fijo")
	private String fijo;
	
	@Column(name="s_correo")
	private String correo;
	
	@Column(name="s_vario")
	private String vario;
	
	@Column(name="s_domicilio")
	private String domicilio;
	
	@Column(name="s_referencia")
	private String referencia;
	
	@Column(name="s_trabaja")
	private String trabaja;
	
	@Column(name="s_centro_trabajo")
	private String centroTrabajo;
	
	@Column(name="s_labores")
	private String labores;
	
	@Column(name="s_direccion_trabajo")
	private String direccionTrabajo;
	
	@Column(name="s_referencia_trabajo")
	private String referenciaTrabajo;
	
	@Column(name="s_jefe_trabajo")
	private String jefeTrabajo;
	
	@Column(name="s_telefono_trabajo")
	private String telefonoTrabajo;
	
	@Column(name="f_registro")
	private String fechaSistema;
	
	@Column(name="c_usuario")
	private String usuario;
	
	@Column(name="s_estado")
	private String estado;
	
	@Column(name="c_usuario_temp")
	private String usuarioTemporal;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_expediente")
	private List<RcExpediente> expedientes;
	
}
