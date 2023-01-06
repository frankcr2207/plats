package com.cdm.dao;

import java.util.List;

import com.cdm.dto.MultaDTO;

public interface MultaDAO {
	public int saveMulta(MultaDTO multaDTO, int abogado, String usuario);
	public List<?> getAllMultas(String usuario);
	public List<?> searchMultas(String parametro);
	public List<?> getAllMultasCoor();
	public List<?> findMulta(int id);
	public int saveTramite(int id, String accion, String texto, String usuario);
	public List<?> searchMultasCoor(String parametro);
}
