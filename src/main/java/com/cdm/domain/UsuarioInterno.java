package com.cdm.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Table(name="usuarios")
@Entity
@Data
public class UsuarioInterno {
	
	@Id
	@Column(name="c_usuario")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String usuario;
	
	@Column(name="s_nombres")
	private String nombres;
	
	@Column(name="s_apellido_paterno")
	private String apePaterno;
	
	@Column(name="s_apellido_materno")
	private String apeMaterno;
	
	@Column(name="s_dni")
	private String dni;
	
	@Column(name="c_especialidad")
	private String especialidad;
	
	@Column(name="s_password")
	private String clave;
	
	@Column(name="s_activo")
	private String estado;
	
	@Column(name="idperfil")
	private Integer perfil;
	
	@OneToMany(mappedBy = "usuarioInterno")
	private List<CdgDocumento> cdg;

}
