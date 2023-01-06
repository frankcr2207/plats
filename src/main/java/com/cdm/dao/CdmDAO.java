package com.cdm.dao;

import java.util.List;

import com.cdm.entities.Cdm;

public interface CdmDAO {
	public List<Cdm> getCdms();
	public List<Cdm> getCdmAsistente(String asistente);
}
