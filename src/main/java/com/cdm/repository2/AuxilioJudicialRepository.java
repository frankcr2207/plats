package com.cdm.repository2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cdm.domain2.FechaHora;
import com.cdm.domain2.AuxilioJudicial;

@Repository
public interface AuxilioJudicialRepository extends JpaRepository<AuxilioJudicial, Integer>{
	
	public List<AuxilioJudicial> findByEstadoInAndSedeIn(List<String> estados, List<String> sede);
	
	public List<AuxilioJudicial> findByFechaSistemaBetweenAndSedeIn(LocalDateTime inicio, LocalDateTime fin, List<String> sede);
	
	@Query("select distinct now() as fechaHora from AuxilioJudicial")
	public FechaHora getFechaHoraActual();
	
}
