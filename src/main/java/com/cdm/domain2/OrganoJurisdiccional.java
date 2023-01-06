package com.cdm.domain2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="organo_jurisdiccional")
@Entity
@Data
public class OrganoJurisdiccional {
	

	@Id
	@Column(name="c_org_jurisd")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
	@Column(name="x_nom_org_jurisd")
	private String denominacion;
	
}
