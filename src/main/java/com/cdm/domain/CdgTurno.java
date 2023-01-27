package com.cdm.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Table(name="cdg_programacion")
@Entity
@Data
public class CdgTurno {
	
	@Id
	@Column(name="id_programacion")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="c_sede")
	private String sede;
	
	@Column(name="c_usuario")
	private String asistenteCdg;
	
	@Column(name="f_fecha")
	private LocalDateTime fecTurno;
	
	@Column(name="c_usuario_asigna")
	private String jefeCdg;
	
	@Column(name="s_ip_pc")
	private String ip;
	
	@CreationTimestamp
	@Column(name="f_sistema", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime fecSistema;
	
	@Column(name="f_baja")
	private LocalDateTime fecBaja;
	
	@Column(name="c_usuario_baja")
	private String jefeCdgBaja;
	
	@Column(name="s_ip_pc_baja")
	private String ipBaja;
	
}
