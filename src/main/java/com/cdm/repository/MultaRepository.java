package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdm.domain.Multa;
import com.cdm.domain.FechaHora;

public interface MultaRepository extends JpaRepository<Multa, Integer>{
	
	List<Multa> findBySecretarioUsuario(String secretario);
	
	List<Multa> findBySecretarioUsuarioAndNombresContainingOrApellidosContaining(String secretario, String parametro, String parametro2);
	
	List<Multa> findByEstado(String estado);
	
	List<Multa> findByNombresContainingOrApellidosContaining(String parametro, String parametro2);
	
	@Query("select distinct now() as fechaHora from Multa")
	public FechaHora getFechaHoraActual();
	
	List<Multa> findByDocumentoAndNumExpAndAnioExpAndIncExpAndNumResolucion(
		String documento, String numExp, String anioExp, String incExp, Integer numResolucion);
	
}
