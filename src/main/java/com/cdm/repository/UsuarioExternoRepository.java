package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.UsuarioExterno;

public interface UsuarioExternoRepository extends JpaRepository<UsuarioExterno, Integer>{

	public List<UsuarioExterno> findByNombresContainingOrApellidosContaining(String parametro, String parametro2);
	
}
