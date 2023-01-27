package com.cdm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdm.domain.CdgDocumento;
import com.cdm.domain2.FechaHora;

public interface CdgDocumentoRepository extends JpaRepository<CdgDocumento, Integer>{
	
	@Query("select distinct now() as fechaHora from CdgDocumento")
	public FechaHora getFechaHoraActual();
	
	List<CdgDocumento> findBySuperiorAndEstado(String superior, String estado);
	
	List<CdgDocumento> findBySedeIdAndEstado(String sede, String estado);
	
	List<CdgDocumento> findByFecSistemaBetween(LocalDateTime fecInicio, LocalDateTime fecFin);
	
	List<CdgDocumento> findByExpedienteContainingOrUsuarioExternoNombresContaining(String parametro, String parametro2);
	
	List<CdgDocumento> findByUsuarioInternoAndFecSistemaBetween(String usuario, LocalDateTime fecInicio, LocalDateTime fecFin);
	
	List<CdgDocumento> findByUsuarioInternoAndEstadoAndSuperior(String usuario, String estado, String superior);
	
	List<CdgDocumento> findByUsuarioInternoAndExpedienteContainingOrUsuarioExternoNombresContaining(String usuario, String parametro, String parametro2);

}
