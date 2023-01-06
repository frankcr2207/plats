package com.cdm.repository2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdm.domain2.FechaHora;
import com.cdm.domain2.Salvaguardia;

public interface SalvaguardiaRepository extends JpaRepository<Salvaguardia, Integer>{
	
	List<Salvaguardia> findByEstadoInAndSedeIn(List<String> estados, List<String> sedes);
	
	List<Salvaguardia> findByFechaSistemaBetweenAndSedeIn(LocalDateTime inicio, LocalDateTime fin, List<String> sedes);
	
	@Query("select distinct now() as fechaHora from Salvaguardia")
	public FechaHora getFechaHoraActual();
}
