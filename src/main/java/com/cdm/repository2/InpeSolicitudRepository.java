package com.cdm.repository2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdm.domain2.FechaHora;
import com.cdm.domain2.InpeSolicitud;

public interface InpeSolicitudRepository extends JpaRepository<InpeSolicitud, Integer>{

	List<InpeSolicitud> findBySedeInAndEstadoIn(List<String> sedes, List<String> estados);
	
	List<InpeSolicitud> findByFechaSistemaBetweenAndSedeIn(LocalDateTime inicio, LocalDateTime fin, List<String> sedes);
	
	@Query("select distinct now() as fechaHora from InpeSolicitud")
	public FechaHora getFechaHoraActual();
}
