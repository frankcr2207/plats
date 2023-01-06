package com.cdm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdm.domain.Audiencia;
import com.cdm.domain2.FechaHora;

public interface AudienciaRepository extends JpaRepository<Audiencia, Integer>{

	@Query("select distinct now() as fechaHora from Audiencia")
	public FechaHora getFechaHoraActual();
	
	Optional<Audiencia> findByExpedienteAndIdInstanciaAndEspecialidadAndFecAudiencia(String expediente, String idInstancia, String especialidad, LocalDateTime fecAdudiencia);
	
	List<Audiencia> findByIdInstanciaInAndFecAudienciaBetween(List<String> instancias, LocalDateTime inicio, LocalDateTime fin);
}
