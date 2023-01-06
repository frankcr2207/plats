package com.cdm.domain2;

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

@Table(name="rectificacion_partida")
@Entity
@Data
public class RecPartida {
	
	@Id
	@Column(name="n_id_rectificacion")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="s_documento_demandante")
	private String documento;
	
	@Column(name="s_nombres_demandante")
	private String nombres;
	
	@Column(name="s_paterno_demandante")
	private String apePaterno;
	
	@Column(name="s_materno_demandante")
	private String apeMaterno;
	
	@Column(name="c_sede")
	private String sede;
	
	@Column(name="f_sistema")
	private String fechaSistema;
	
	@Column(name="c_usuario_csjar")
	private String usuario;
	
	@Column(name="s_estado")
	private String estado;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "n_id_rectificacion")
	private List<RecPartidaArchivo> archivoRecPartidaS;
}
