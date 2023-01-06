package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.Instancia;

public interface InstanciaRepository extends JpaRepository<Instancia, String>{

	List<Instancia> findBySedeIdAndSalvaguardiaIsNotNull(String sede);
	
	List<Instancia> findBySedeId(String sede);
	
	List<Instancia> findBySedeIdAndEspecialidad(String sede, String especialidad);
	
}
