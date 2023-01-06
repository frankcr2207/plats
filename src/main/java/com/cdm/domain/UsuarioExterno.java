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

@Table(name="usuario")
@Entity
@Data
public class UsuarioExterno {
	
	@Id
	@Column(name="n_id_usuario")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_nombres")
	private String nombres;
	
	@Column(name="s_apellidos")
	private String apellidos;
	
	@Column(name="s_documento")
	private String documento;
	
	@Column(name="s_direccion")
	private String direccion;
	
	@Column(name="s_celular")
	private String celular;
	
	@Column(name="s_email")
	private String email;
	
	@Column(name="s_clave")
	private String clave;
	
	@OneToMany(mappedBy = "usuarioExterno")
	private List<CdgDocumento> cdg;
}
