package com.cdm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdm.domain.CdgTurno;

public interface CdgTurnoRepository extends JpaRepository<CdgTurno, Integer>{
	
	CdgTurno findByAsistenteCdgAndSedeAndFecTurnoAndFecBajaIsNull(String asistenteCdg, String sede, LocalDateTime fecTurno);
	
	List<CdgTurno> findBySedeAndFecTurnoBetweenAndFecBajaIsNull(String sede, LocalDateTime inicio, LocalDateTime fin);
	
}
