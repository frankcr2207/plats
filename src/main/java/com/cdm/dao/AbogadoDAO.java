package com.cdm.dao;

import com.cdm.dto.MultaDTO;

public interface AbogadoDAO {
	public int saveAbogado(MultaDTO multaDTO);
	public int findIdAbogado(String dni);
	public int countAbogado(String dni);
	public int updateAbogado(MultaDTO multaDTO);
}
