package com.cdm.serviImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.dao.AbogadoDAO;
import com.cdm.dao.MultaDAO;
import com.cdm.dto.MultaDTO;

@Service
public class MultaService {
	
	@Autowired
	AbogadoDAO abogadoDAO;
	@Autowired
	MultaDAO multaDAO;
	
	public int gestionNuevaMulta(MultaDTO multaDTO, String usuario) {
		int id = 0;
		return multaDAO.saveMulta(multaDTO, id, usuario);
	}
}
