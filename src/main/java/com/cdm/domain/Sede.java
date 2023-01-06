package com.cdm.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;


@Table(name="sede")
@Entity
@Data
public class Sede {
	
	@Id
	@Column(name="c_sede")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@Column(name="s_sede")
	private String denominacion;
	
	@OneToMany(mappedBy = "sede")
	private List<Instancia> instancias;
}
