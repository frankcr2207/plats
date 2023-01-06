package com.cdm.dao;

import java.util.List;

import com.cdm.entities.Instancia;

public interface InstanciaDAO {
	public List<Instancia> getInstancias(String sede);
	public List<Instancia> getInstanciasNCPP(String sede);
	public List<Instancia> getInstanciasCDM(String cdm);
	public List<Instancia> getInstanciasSecretario(String secretario);
}
